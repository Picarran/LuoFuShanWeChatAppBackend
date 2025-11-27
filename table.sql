-- =====================================
-- 用户表 user
-- =====================================

DROP TABLE IF EXISTS post_comment;

DROP TABLE IF EXISTS user_post;

DROP TABLE IF EXISTS user_checkin;

DROP TABLE IF EXISTS checkin_location;

DROP TABLE IF EXISTS user_token;

DROP TABLE IF EXISTS resource;

DROP TABLE IF EXISTS user;



CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    open_id VARCHAR(64) NOT NULL COMMENT '微信openid',
    nickname VARCHAR(64) NULL COMMENT '昵称',
    avatar_url VARCHAR(255) NULL COMMENT '头像链接',
    points INT DEFAULT 0 COMMENT '当前积分',
    weekly_checkin_count INT DEFAULT 0 COMMENT '本周打卡数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uniq_open_id (open_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 用户登录 token 表 user_token
-- =====================================

CREATE TABLE user_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    token VARCHAR(255) NOT NULL COMMENT '唯一登录 token',
    expired_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE KEY uniq_token (token),
    INDEX idx_token_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- Resource 表
-- =====================================

CREATE TABLE resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，资源唯一标识',
    type ENUM('景点','住宿','餐饮','商家') NOT NULL COMMENT '类别类型',
    name VARCHAR(64) NOT NULL COMMENT '名称',
    cover_img VARCHAR(255) NULL COMMENT '封面图链接',
    latitude DOUBLE NULL COMMENT '地理纬度',
    longitude DOUBLE NULL COMMENT '地理经度',
    hot_score INT DEFAULT 0 COMMENT '热度（排序权重）',
    content_json JSON NULL COMMENT '组件化图文内容 JSON 数据',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 打卡点表 checkin_location
-- =====================================

CREATE TABLE checkin_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '景点ID',
    name VARCHAR(64) NOT NULL COMMENT '地点名称',
    latitude DOUBLE NULL COMMENT '纬度',
    longitude DOUBLE NULL COMMENT '经度',
    score INT NOT NULL COMMENT '单次打卡奖励积分',
    today_has_checkin BIGINT NOT NULL COMMENT '今日总打卡数',
    cover_img VARCHAR(255) NULL COMMENT '封面图',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 用户打卡记录 user_checkin
-- =====================================

CREATE TABLE user_checkin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    location_id BIGINT NOT NULL COMMENT '景点ID',
    checkin_time DATETIME NOT NULL COMMENT '打卡时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    CONSTRAINT fk_checkin_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_checkin_location FOREIGN KEY (location_id) REFERENCES checkin_location(id) ON DELETE CASCADE,
    UNIQUE KEY uniq_user_location (user_id, location_id),
    INDEX idx_user_checkin_user (user_id),
    INDEX idx_user_checkin_location (location_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 用户动态 user_post
-- =====================================

CREATE TABLE user_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '动态ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    location_id BIGINT COMMENT '景点ID',
    content TEXT NULL COMMENT '文本内容',
    images JSON NULL COMMENT '图片数组',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_location FOREIGN KEY (location_id) REFERENCES checkin_location(id) ON DELETE SET NULL,
    INDEX idx_post_user (user_id),
    INDEX idx_post_location (location_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 用户评论 post_comment
-- =====================================

CREATE TABLE post_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '动态ID',
    user_id BIGINT NOT NULL COMMENT '评论者用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES user_post(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_comment_post (post_id),
    INDEX idx_comment_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
