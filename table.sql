-- =====================================
-- 用户表 user
-- =====================================

DROP TABLE IF EXISTS post_comment;
DROP TABLE IF EXISTS user_post;
DROP TABLE IF EXISTS user_checkin;
DROP TABLE IF EXISTS checkin_location;
DROP TABLE IF EXISTS user_token;
DROP TABLE IF EXISTS merchant_token;
DROP TABLE IF EXISTS merchant;
DROP TABLE IF EXISTS resource;
DROP TABLE IF EXISTS user_exchange;
DROP TABLE IF EXISTS mall_item;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS admin_token;
DROP TABLE IF EXISTS admin_config;

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
    content_json TEXT NULL COMMENT '组件化图文内容 JSON 数据',
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
    UNIQUE KEY uniq_user_location (user_id, location_id, checkin_time),
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
    post_time DATETIME NOT NULL COMMENT '发布动态时间',
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

-- =====================================
-- 商家端账号表 merchant
-- =====================================
CREATE TABLE merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商家ID',
    resource_id BIGINT NOT NULL COMMENT '关联的商家资源ID',
    name VARCHAR(64) NOT NULL COMMENT '商家名称',
    type ENUM('景点','住宿','餐饮','商家') NOT NULL COMMENT '商家类型',
    username VARCHAR(64) NOT NULL COMMENT '登录账号',
    password VARCHAR(128) NOT NULL COMMENT '加密后的密码',
    contact_name VARCHAR(64) DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(64) DEFAULT NULL COMMENT '联系电话',
    address VARCHAR(255) DEFAULT NULL COMMENT '商家地址',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    UNIQUE KEY uk_merchant_username (username),
    UNIQUE KEY uk_merchant_resource (resource_id),

    CONSTRAINT fk_merchant_resource
        FOREIGN KEY (resource_id) REFERENCES resource(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家账号表';

-- =====================================
-- 商家Token表 merchant_token
-- =====================================
CREATE TABLE merchant_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    token VARCHAR(255) NOT NULL COMMENT '登录Token',
    expired_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_merchant_token (token),
    KEY idx_merchant_id (merchant_id),
    CONSTRAINT fk_merchant_token_merchant
        FOREIGN KEY (merchant_id) REFERENCES merchant(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家登录Token表';

-- =====================================
-- 兑换商城奖品表 mall_item
-- =====================================

CREATE TABLE mall_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '奖品ID',
    name VARCHAR(64) NOT NULL COMMENT '奖品名称',
    points_cost INT NOT NULL COMMENT '所需积分',
    cover_img VARCHAR(255) NULL COMMENT '奖品图片链接',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 用户兑换记录表 user_exchange
-- =====================================

CREATE TABLE user_exchange (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '兑换记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    item_id BIGINT NOT NULL COMMENT '兑换的奖品ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '兑换卡密（兑换凭证）',
    is_redeemed TINYINT NOT NULL DEFAULT 0 COMMENT '是否已兑换（0否/1是）',
    place_name VARCHAR(64) NULL COMMENT '兑换地点名称',
    redeemed_at DATETIME NULL COMMENT '核销时间',
    merchant_id BIGINT NULL COMMENT '核销商家ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    delflag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    CONSTRAINT fk_exchange_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    CONSTRAINT fk_exchange_item FOREIGN KEY (item_id) REFERENCES mall_item(id) ON DELETE CASCADE,
    CONSTRAINT fk_exchange_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE SET NULL,
    INDEX idx_exchange_user (user_id),
    INDEX idx_exchange_item (item_id),
    INDEX idx_exchange_merchant (merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================
-- 管理解锁密码配置表 admin_config
-- =====================================
CREATE TABLE admin_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unlock_password VARCHAR(128) NOT NULL COMMENT '管理端解锁密码（加密存储）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理端配置表';

-- =====================================
-- 管理端会话表 admin_token
-- =====================================
CREATE TABLE admin_token (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  token VARCHAR(64) NOT NULL UNIQUE COMMENT '管理端会话Token',
  expire_time DATETIME NOT NULL COMMENT '过期时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='管理端解锁会话表';


-- 插入测试用户
INSERT INTO user (open_id, nickname, avatar_url, points, weekly_checkin_count) VALUES
('wx_openid_1', '用户1', 'https://example.com/avatar1.jpg', 100, 2),
('wx_openid_2', '用户2', 'https://example.com/avatar2.jpg', 150, 1),
('wx_openid_3', '用户3', 'https://example.com/avatar3.jpg', 80, 3);

INSERT INTO resource (type, name, cover_img, latitude, longitude, hot_score, content_json) VALUES
('景点', '九寨沟', 'https://example.com/images/jiuzhaigou.jpg', 33.2529, 103.9186, 95,
 '[{"type":"text","content":"九寨沟位于四川省阿坝藏族羌族自治州，以湖泊和瀑布闻名。"}]'),
('住宿', '成都锦江宾馆', 'https://example.com/images/jinjiang_hotel.jpg', 30.6586, 104.0648, 80,
 '[{"type":"text","content":"成都锦江宾馆提供舒适的住宿环境，方便游客出行。"}]'),
('餐饮', '成都火锅店', 'https://example.com/images/hotpot.jpg', 30.6628, 104.0719, 75,
 '[{"type":"text","content":"正宗成都火锅，辣而不燥，食材新鲜。"}]'),
('商家', '成都茶叶店', 'https://example.com/images/tea_shop.jpg', 30.6550, 104.0665, 60,
 '[{"type":"text","content":"提供各类优质茶叶和茶具，适合茶文化爱好者。"}]'),
('景点', '乐山大佛', 'https://example.com/images/leshan_buddha.jpg', 29.5585, 103.7655, 90,
 '[{"type":"text","content":"乐山大佛是世界上最大的石刻坐佛，景区历史悠久。"}]');

-- 插入打卡点
INSERT INTO checkin_location (name, latitude, longitude, score, today_has_checkin, cover_img) VALUES
('九寨沟', 33.2529, 103.9186, 10, 0, 'https://example.com/images/jiuzhaigou.jpg'),   -- id = 1
('锦江宾馆', 30.6586, 104.0648, 5, 0, 'https://example.com/images/jinjiang_hotel.jpg'), -- id = 2
('成都火锅店', 30.6628, 104.0719, 4, 0, 'https://example.com/images/hotpot.jpg'),         -- id = 3
('成都茶叶店', 30.6550, 104.0665, 3, 0, 'https://example.com/images/tea_shop.jpg'),       -- id = 4
('乐山大佛', 29.5585, 103.7655, 8, 0, 'https://example.com/images/leshan_buddha.jpg'),    -- id = 5
('黄龙风景区', 32.7547, 103.8223, 9, 0, 'https://example.com/images/huanglong.jpg'),      -- id = 6（新增）
('都江堰景区', 31.0021, 103.6052, 7, 0, 'https://example.com/images/dujiangyan.jpg');      -- id = 7（新增）

-- 插入商城奖品
INSERT INTO mall_item (name, points_cost, cover_img) VALUES
('景点门票', 50, 'https://example.com/images/ticket.jpg'),
('酒店住宿', 200, 'https://example.com/images/hotel_reward.jpg'),
('火锅代金券', 100, 'https://example.com/images/hotpot_voucher.jpg'),
('茶叶礼盒', 150, 'https://example.com/images/tea_gift_box.jpg'),
('大佛纪念品', 80, 'https://example.com/images/buddha_memorial.jpg');

-- 插入兑换记录
INSERT INTO user_exchange (user_id, item_id, code, is_redeemed, place_name) VALUES
(1, 1, 'CODE1234567890', 0, '九寨沟景区兑换处'),
(1, 3, 'CODE0987654321', 1, '成都火锅店'),
(2, 2, 'CODE1122334455', 0, '锦江宾馆前台'),
(3, 4, 'CODE2233445566', 1, '成都茶叶店'),
(3, 5, 'CODE3344556677', 0, '乐山大佛景区商店');

-- 插入初始管理端解锁密码（密码为 "admin123456" 的 MD5 哈希值）
INSERT INTO admin_config (unlock_password) VALUES
(MD5('admin123456'));
