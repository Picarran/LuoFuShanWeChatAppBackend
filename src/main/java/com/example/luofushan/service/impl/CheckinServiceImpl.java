package com.example.luofushan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.dao.entity.CheckinLocation;
import com.example.luofushan.dao.entity.User;
import com.example.luofushan.dao.entity.UserCheckin;
import com.example.luofushan.dao.mapper.CheckinLocationMapper;
import com.example.luofushan.dao.mapper.UserCheckinMapper;
import com.example.luofushan.dao.mapper.UserMapper;
import com.example.luofushan.dto.req.CheckinRankPageReq;
import com.example.luofushan.dto.req.UserCheckinHistoryReq;
import com.example.luofushan.dto.req.UserCheckinReq;
import com.example.luofushan.dto.resp.*;
import com.example.luofushan.security.UserContext;
import com.example.luofushan.service.CheckinService;
import com.example.luofushan.util.TimeUtil;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.luofushan.common.constant.RedisCacheConstant.*;

@Service
public class CheckinServiceImpl implements CheckinService {

    @Resource
    private CheckinLocationMapper checkinLocationMapper;
    @Resource
    private UserCheckinMapper userCheckinMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<CheckinLocationListResp> getAllLocations() {
        // 查询所有未逻辑删除的打卡点
        LambdaQueryWrapper<CheckinLocation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckinLocation::getDelflag, 0);

        List<CheckinLocation> locations = checkinLocationMapper.selectList(wrapper);

        return locations.stream().map(loc -> CheckinLocationListResp.builder()
                .id(loc.getId())
                .name(loc.getName())
                .latitude(loc.getLatitude())
                .longitude(loc.getLongitude())
                .score(loc.getScore())
                .todayHasCheckin(getLocationCheckinCount(loc.getId()))
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public Page<UserCheckinHistoryResp> getUserHistory(UserCheckinHistoryReq req) {
        req.initDefault();
        int offset = (req.getPage() - 1) * req.getSize();
        List<UserCheckinHistoryResp> records = userCheckinMapper.selectHistoryByUserId(UserContext.getUserId(), offset, req.getSize());
        int total = userCheckinMapper.countByUserId(UserContext.getUserId());

        Page<UserCheckinHistoryResp> page = new Page<>(req.getPage(), req.getSize());
        page.setRecords(records);
        page.setTotal(total);
        page.setPages((total + req.getSize() - 1) / req.getSize());

        return page;
    }

    @Override
    @Transactional
    public UserCheckinResp doUserCheckin(UserCheckinReq req) {

        // 1. 校验景点是否存在, 校验用户
        CheckinLocation loc = checkinLocationMapper.selectById(req.getLocationId());
        if (loc == null) {
            throw LuoFuShanException.checkinLocationNotExists();
        }
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new LuoFuShanException("未登录");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new LuoFuShanException("用户不存在");
        }

        // 2. 插入打卡记录（利用 UNIQUE KEY 防重复）
        UserCheckin uc = UserCheckin.builder()
                .userId(UserContext.getUserId())
                .locationId(req.getLocationId())
                .checkinTime(req.getCheckinTime())
                .build();

        try {
            userCheckinMapper.insert(uc);
        } catch (DuplicateKeyException e) {
            throw LuoFuShanException.alreadyHit();
        }

        // 3. 景点今日打卡数 + 1
//        checkinLocationMapper.incrementTodayCount(req.getLocationId());
        increaseLocationCheckinCount(req.getLocationId());

        // 4. 获取更新后的今日打卡数
//        Long todayCount = checkinLocationMapper.selectTodayCount(req.getLocationId());
        Long todayCount = getLocationCheckinCount(req.getLocationId());

        // 5.1. 更新用户积分
        int score = loc.getScore();
        user.setPoints(user.getPoints() + score);
//        user.setWeeklyCheckinCount(user.getWeeklyCheckinCount() + 1);
        userMapper.updateById(user);

        // 5.2. 更新用户打卡数
        increaseUserCheckinCount(userId);

        // 6. 构造返回对象
        return UserCheckinResp.builder()
                .id(uc.getId())
                .locationId(loc.getId())
                .locationName(loc.getName())
                .checkinTime(req.getCheckinTime())
                .score(loc.getScore())
                .todayHasCheckin(todayCount)
                .build();
    }

    private void increaseLocationCheckinCount(Long id) {
        CheckinKeyConfig config = new CheckinKeyConfig(LOCATION_CHECKIN_KEY, TimeUtil::remainSecondsToday);
        String key = config.keyPrefix + id;

        // 原子自增
        Long count = stringRedisTemplate.opsForValue().increment(key);

        // 第一次创建 key 时才设置过期时间
        if (count != null && count == 1L) {
            long ttl = config.ttlFunc.apply(LocalDateTime.now());
            stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        }
    }

    private void increaseUserCheckinCount(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        List<CheckinKeyConfig> configs = List.of(
                new CheckinKeyConfig(USER_CHECKIN_DAY_KEY, USER_CHECKIN_RANK_DAY_KEY,  TimeUtil::remainSecondsToday),
                new CheckinKeyConfig(USER_CHECKIN_WEEK_KEY, USER_CHECKIN_RANK_WEEK_KEY, TimeUtil::remainSecondsThisWeek),
                new CheckinKeyConfig(USER_CHECKIN_MONTH_KEY, USER_CHECKIN_RANK_MONTH_KEY, TimeUtil::remainSecondsThisMonth)
        );

        for (CheckinKeyConfig config : configs) {
            String key = config.keyPrefix + userId;

            // 原子自增
            Long count = stringRedisTemplate.opsForValue().increment(key);

            // 第一次创建 key 时才设置过期时间
            long ttl = config.ttlFunc.apply(now);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(key, ttl, TimeUnit.SECONDS);
            }

            String timeSuffix = TimeUtil.formatByPrefix(config.rankKeyPrefix, now);
            String rankKey = config.rankKeyPrefix + timeSuffix;
            String member = String.valueOf(userId);
            stringRedisTemplate.opsForZSet().add(rankKey, member, count);
            stringRedisTemplate.expire(rankKey, ttl, TimeUnit.SECONDS);
        }
    }

    @Override
    public long getLocationCheckinCount(Long id) {
        String key = LOCATION_CHECKIN_KEY + id;
        if(!stringRedisTemplate.hasKey(key)) {
            return 0;
        }
        return Long.parseLong(stringRedisTemplate.opsForValue().get(key));
    }

    @Override
    public long getUserCheckinDayCount(Long id) {
        String key = USER_CHECKIN_DAY_KEY + id;
        if(!stringRedisTemplate.hasKey(key)) {
            return 0;
        }
        return Long.parseLong(stringRedisTemplate.opsForValue().get(key));
    }

    @Override
    public long getUserCheckinWeekCount(Long id) {
        String key = USER_CHECKIN_WEEK_KEY + id;
        if(!stringRedisTemplate.hasKey(key)) {
            return 0;
        }
        return Long.parseLong(stringRedisTemplate.opsForValue().get(key));
    }

    @Override
    public long getUserCheckinMonthCount(Long id) {
        String key = USER_CHECKIN_MONTH_KEY + id;
        if(!stringRedisTemplate.hasKey(key)) {
            return 0;
        }
        return Long.parseLong(stringRedisTemplate.opsForValue().get(key));
    }

    private static class CheckinKeyConfig {
        String keyPrefix;
        String rankKeyPrefix;
        Function<LocalDateTime, Long> ttlFunc;

        CheckinKeyConfig(String keyPrefix, String rankKeyPrefix, Function<LocalDateTime, Long> ttlFunc) {
            this.keyPrefix = keyPrefix;
            this.rankKeyPrefix = rankKeyPrefix;
            this.ttlFunc = ttlFunc;
        }

        CheckinKeyConfig(String keyPrefix, Function<LocalDateTime, Long> ttlFunc) {
            this.keyPrefix = keyPrefix;
            this.ttlFunc = ttlFunc;
        }
    }

    @Override
    public Page<CheckinRankPageResp> getRank(CheckinRankPageReq req) {
        req.initDefault();
        List<String> types = List.of("day", "week", "month");
        if(StringUtil.isNullOrEmpty(req.getType()) || !types.contains(req.getType())) {
            throw new LuoFuShanException("类型错误: day/week/month");
        }

        // 1. 选择排行榜 key 前缀
        String rankKeyPrefix;
        switch (req.getType()) {
            case "day" -> rankKeyPrefix = USER_CHECKIN_RANK_DAY_KEY;
            case "week" -> rankKeyPrefix = USER_CHECKIN_RANK_WEEK_KEY;
            case "month" -> rankKeyPrefix = USER_CHECKIN_RANK_MONTH_KEY;
            default -> throw new LuoFuShanException("类型错误");
        }

        // 2. 生成时间后缀
        LocalDateTime now = LocalDateTime.now();
        String timeSuffix = TimeUtil.formatByPrefix(rankKeyPrefix, now);
        String rankKey = rankKeyPrefix + timeSuffix;

        // 3. 计算分页
        int page = req.getPage();
        int size = req.getSize();
        int start = (page - 1) * size;
        int end = start + size - 1;

        // 4. 查 ZSet
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet()
                        .reverseRangeWithScores(rankKey, start, end);

        if (tuples == null || tuples.isEmpty()) {
            return new Page<>(page, size, 0L);
        }

        // 5. 收集 userId
        List<Long> userIds = tuples.stream()
                .map(t -> Long.valueOf(t.getValue()))
                .toList();

        // 6. 批量查用户
        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 7. 组装返回
        List<CheckinRankPageResp> records = new ArrayList<>();
        long rank = start + 1;

        for (ZSetOperations.TypedTuple<String> t : tuples) {
            Long userId = Long.valueOf(t.getValue());
            Long count = t.getScore().longValue();
            User user = userMap.get(userId);

            if (user == null) continue;

            records.add(CheckinRankPageResp.builder()
                    .userId(userId)
                    .nickname(user.getNickname())
                    .avatarUrl(user.getAvatarUrl())
                    .type(req.getType())
                    .checkinCount(count)
                    .rank(rank++)
                    .build());
        }

        // 8. 总数
        Long total = stringRedisTemplate.opsForZSet().zCard(rankKey);

        Page<CheckinRankPageResp> resp = new Page<>(page, size, total);
        resp.setRecords(records);
        return resp;
    }

    @Override
    public CheckinRankMeResp getRankMe(String type) {
        Long userId = UserContext.getUserId();
        if(userId==null) throw new LuoFuShanException("未登录");

        List<String> types = List.of("day", "week", "month");
        if (StringUtil.isNullOrEmpty(type) || !types.contains(type)) {
            throw new LuoFuShanException("类型错误: day/week/month");
        }

        // 1. 选择排行榜 key 前缀
        String rankKeyPrefix;
        switch (type) {
            case "day" -> rankKeyPrefix = USER_CHECKIN_RANK_DAY_KEY;
            case "week" -> rankKeyPrefix = USER_CHECKIN_RANK_WEEK_KEY;
            case "month" -> rankKeyPrefix = USER_CHECKIN_RANK_MONTH_KEY;
            default -> throw new LuoFuShanException("类型错误");
        }

        // 2. 生成 key
        LocalDateTime now = LocalDateTime.now();
        String suffix = TimeUtil.formatByPrefix(rankKeyPrefix, now);
        String rankKey = rankKeyPrefix + suffix;

        String member = String.valueOf(userId);

        // 3. 查排名（0-based）
        Long rank = stringRedisTemplate.opsForZSet()
                .reverseRank(rankKey, member);

        // 没上榜 idea提示不可达，实则是有可能的
        if (rank == null) {
            return CheckinRankMeResp.builder()
                    .rank(null)        // 转成 1-based
                    .checkinCount(0L)
                    .type(type)
                    .build();
        }

        // 4. 查分数（打卡次数）
        Double score = stringRedisTemplate.opsForZSet()
                .score(rankKey, member);

        return CheckinRankMeResp.builder()
                .rank(rank + 1)        // 转成 1-based
                .checkinCount(score == null ? 0L : score.longValue())
                .type(type)
                .build();
    }
}