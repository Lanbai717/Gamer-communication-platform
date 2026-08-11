/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : badminton_db

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 12/05/2026 17:03:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `aid` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `venue_id` int NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `creator_id` int NULL DEFAULT NULL,
  `max_players` int NULL DEFAULT NULL,
  `current_players` int NULL DEFAULT 0,
  `sport_type` int NULL DEFAULT 1,
  `level_require` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`aid`) USING BTREE,
  INDEX `venue_id`(`venue_id` ASC) USING BTREE,
  INDEX `creator_id`(`creator_id` ASC) USING BTREE,
  CONSTRAINT `activity_ibfk_1` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`vid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `activity_ibfk_2` FOREIGN KEY (`creator_id`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity
-- ----------------------------
INSERT INTO `activity` VALUES (1, '2v2混双', NULL, '霓虹厅羽球馆', '2026-03-27 09:00:00', '2026-03-27 11:00:00', 3, 8, 3, NULL, '不限', '', 0, '2026-03-27 05:57:27', '2026-05-12 15:41:08');
INSERT INTO `activity` VALUES (2, '休闲养生球局', NULL, '日落之城羽球馆', '2026-05-13 08:00:57', '2026-05-14 11:00:18', 4, 4, 2, NULL, '不限', '养生球，强度不高', 0, '2026-04-21 12:26:02', '2026-05-12 16:28:14');
INSERT INTO `activity` VALUES (3, '泰安打球的来', NULL, '日落之城羽球馆', '2026-05-13 17:00:00', '2026-05-13 22:00:00', 2, 4, 1, 1, '不限', '', 0, '2026-05-12 16:27:51', '2026-05-12 16:28:01');

-- ----------------------------
-- Table structure for activity_participant
-- ----------------------------
DROP TABLE IF EXISTS `activity_participant`;
CREATE TABLE `activity_participant`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `activity_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0,
  `join_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `activity_id`(`activity_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `activity_participant_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `activity` (`aid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `activity_participant_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_participant
-- ----------------------------
INSERT INTO `activity_participant` VALUES (1, 1, 3, 0, '2026-03-27 05:57:48');
INSERT INTO `activity_participant` VALUES (4, 2, 4, 0, '2026-05-12 11:19:24');
INSERT INTO `activity_participant` VALUES (7, 1, 2, 0, '2026-05-12 14:44:13');
INSERT INTO `activity_participant` VALUES (8, 1, 4, 0, '2026-05-12 15:41:08');
INSERT INTO `activity_participant` VALUES (9, 3, 2, 0, '2026-05-12 16:27:55');
INSERT INTO `activity_participant` VALUES (10, 2, 2, 0, '2026-05-12 16:28:14');

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `aid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`aid`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '$2a$10$CMiQADwFcCaczFe3aks0bO5rloTJ9g0y13BeWFt6myrwWtACHWBVa', '管理员', NULL, 0, '2026-03-16 18:27:05', '2026-03-25 07:44:11');

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_uid` int NOT NULL,
  `receiver_uid` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_read` tinyint NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender_receiver`(`sender_uid` ASC, `receiver_uid` ASC) USING BTREE,
  INDEX `idx_receiver_sender`(`receiver_uid` ASC, `sender_uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (1, 4, 2, '你好', 0, '2026-05-12 12:34:07');
INSERT INTO `chat_message` VALUES (2, 2, 4, '你好！', 0, '2026-05-12 12:46:36');
INSERT INTO `chat_message` VALUES (3, 4, 2, '打球吗', 0, '2026-05-12 13:00:11');
INSERT INTO `chat_message` VALUES (4, 4, 2, '你在哪里呀', 0, '2026-05-12 13:15:54');
INSERT INTO `chat_message` VALUES (5, 2, 4, '泰安', 0, '2026-05-12 13:24:45');
INSERT INTO `chat_message` VALUES (6, 4, 2, '好的', 0, '2026-05-12 13:25:06');
INSERT INTO `chat_message` VALUES (7, 4, 2, '明天下午怎么样？', 0, '2026-05-12 13:34:23');
INSERT INTO `chat_message` VALUES (8, 2, 4, '可以呀', 0, '2026-05-12 13:34:35');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `cid` int NOT NULL AUTO_INCREMENT,
  `post_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `parent_id` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`cid`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`pid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 2, '我我我', 0, '2026-03-27 05:21:32');
INSERT INTO `comment` VALUES (2, 3, 4, '有咩有人来呀\n', 0, '2026-05-02 23:40:35');
INSERT INTO `comment` VALUES (3, 3, 2, '泰安有吗？\n', 0, '2026-05-12 13:35:18');

-- ----------------------------
-- Table structure for friend_request
-- ----------------------------
DROP TABLE IF EXISTS `friend_request`;
CREATE TABLE `friend_request`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `from_uid` int NOT NULL COMMENT '发送方uid',
  `to_uid` int NOT NULL COMMENT '接收方uid',
  `message` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '验证信息',
  `status` tinyint NULL DEFAULT 0 COMMENT '0待处理 1同意 2拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_to`(`to_uid` ASC) USING BTREE,
  INDEX `idx_from`(`from_uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friend_request
-- ----------------------------
INSERT INTO `friend_request` VALUES (1, 4, 2, '你好，想和你约球！', 1, '2026-05-12 11:49:04');

-- ----------------------------
-- Table structure for friendship
-- ----------------------------
DROP TABLE IF EXISTS `friendship`;
CREATE TABLE `friendship`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id1` int NOT NULL,
  `user_id2` int NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_pair`(`user_id1` ASC, `user_id2` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friendship
-- ----------------------------
INSERT INTO `friendship` VALUES (1, 2, 4, '2026-05-12 12:00:32');

-- ----------------------------
-- Table structure for match_weight
-- ----------------------------
DROP TABLE IF EXISTS `match_weight`;
CREATE TABLE `match_weight`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `feature_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `weight` double NULL DEFAULT 0.1,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `feature_name`(`feature_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '智能匹配权重配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of match_weight
-- ----------------------------
INSERT INTO `match_weight` VALUES (1, 'skill', 0.4, '2026-05-03 01:01:49');
INSERT INTO `match_weight` VALUES (2, 'geo', 0.3, '2026-05-03 01:01:49');
INSERT INTO `match_weight` VALUES (3, 'active', 0.3, '2026-05-03 01:01:49');

-- ----------------------------
-- Table structure for merchant
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant`  (
  `mid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `company_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `business_license` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `business_hours` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '营业时间',
  `is_submitted` tinyint(1) NULL DEFAULT 0 COMMENT '是否已提交审核',
  `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '设施列表（JSON数组或逗号分隔）',
  `detail_intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '场馆详细介绍',
  `videos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '视频链接，多个用逗号分隔',
  `rules` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '使用规则',
  `venue_photos` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '场馆宣传图片，多张用逗号分隔',
  `pending_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '待审核的修改数据（JSON格式）',
  PRIMARY KEY (`mid`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant
-- ----------------------------
INSERT INTO `merchant` VALUES (1, 'ysdk', '$2a$10$51gv04oi080v8Gdl56xdf.kHyeP2NjeFVmtQLXiStYeTt6mTrVL3K', '幽邃地窟', '魏玲莹', '14502362033', '3980564876@qq.com', '无畏契约区幽邃地窟东路117号', 'http://localhost:8080/uploads/1622c7df-2948-4f78-8bfe-083d9d5d7a99.png', 1, '2026-03-26 22:08:55', '2026-04-24 00:38:08', '[{\"startDay\":1,\"endDay\":7,\"startTime\":\"08:00\",\"endTime\":\"20:40\"}]', 0, '空调', '场馆高度足够高，专用照明设备', '', '不得无故损坏馆内设施', 'http://localhost:8080/uploads/3c84c178-bfc9-42e1-8ab2-a241935190c0.png', '{\"businessHours\":\"[{\\\"startDay\\\":1,\\\"endDay\\\":7,\\\"startTime\\\":\\\"08:00\\\",\\\"endTime\\\":\\\"20:40\\\"}]\"}');
INSERT INTO `merchant` VALUES (2, 'rlzc', '$2a$10$Bk5W0L7rQA0fZjdAFpNpiOl5Eyy78FDBMIVc8sEuDG4MiA1xf8eai', '日落之城羽球馆', '韩善宇', '15467845279', '4369860148@qq.com', '河北省廊坊市日落大道68号', 'http://localhost:8080/uploads/d83a1a26-ee88-4bb0-8739-5cd98e02dfda.png', 1, '2026-03-31 00:18:55', '2026-04-24 00:47:02', '[{\"startDay\":1,\"endDay\":7,\"startTime\":\"08:00\",\"endTime\":\"22:00\"}]', 0, '更衣室，淋浴，自动贩卖机', '有穿线机和专业穿线师', '', '提前一小时预约', 'http://localhost:8080/uploads/86969fcf-f428-42d4-b1fa-53dfee71a9ef.png', '{\"address\":\"河北省廊坊市日落大道68号\"}');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `oid` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` int NULL DEFAULT NULL,
  `venue_id` int NULL DEFAULT NULL,
  `book_date` date NULL DEFAULT NULL,
  `time_slot` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `hours` int NULL DEFAULT NULL,
  `total_price` decimal(10, 2) NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `pay_time` datetime NULL DEFAULT NULL,
  `contact_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `need_coach` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否需要陪练：0-不需要，1-需要',
  `coach_count` int NOT NULL DEFAULT 0 COMMENT '需要的陪练人数',
  PRIMARY KEY (`oid`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `venue_id`(`venue_id` ASC) USING BTREE,
  CONSTRAINT `order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `order_ibfk_2` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`vid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 'ORD177747428031309dc', 4, 3, '2026-04-30', '08:00-10:00', 2, 40.00, 1, '2026-04-29 22:51:20', '2026-04-30 00:17:49', '澜白', '19931761637', 0, 0);
INSERT INTO `order` VALUES (2, 'ORD177747943078613db', 4, 3, '2026-04-07', '08:00-10:00', 2, 40.00, 2, '2026-04-30 00:17:10', NULL, '澜白', '19931761637', 0, 0);
INSERT INTO `order` VALUES (3, 'ORD17774866486997e85', 4, 7, '2026-04-30', '20:00-22:00', 2, 100.00, 3, '2026-04-30 02:17:28', '2026-04-30 02:17:35', '澜白', '19931761637', 0, 0);

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `pid` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `view_count` int NULL DEFAULT 0,
  `like_count` int NULL DEFAULT 0,
  `comment_count` int NULL DEFAULT 0,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`pid`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 4, '找球搭子', '找女生双打', 46, 5, 1, 0, '2026-03-27 04:11:44', '2026-05-02 23:40:50');
INSERT INTO `post` VALUES (2, 4, '吐槽一下今天的局', '本来约好的球局，临时放鸽子的也太多了… 有没有靠谱的长期队友一起固定组队？', 1, 0, 0, 0, '2026-05-02 23:39:29', '2026-05-02 23:39:34');
INSERT INTO `post` VALUES (3, 4, '求拼场地！', '订了幽邃地窟球馆的场，本周四晚上 7-9 点，缺 2 个人 AA 场地费，水平不限，来凑数就行！', 3, 0, 2, 0, '2026-05-02 23:40:19', '2026-05-12 13:35:18');

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子点赞记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------
INSERT INTO `post_like` VALUES (49, 1, 4, '2026-03-31 11:13:53');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `uid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `skill_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '省',
  `city` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '市',
  `district` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '区',
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (2, 'dyf_1122', '$2a$10$FMGMK3xNU7qR/Ispc0N.aOU2W2Th8m3UyL1q0sO16KhKcnQLrTvh6', '困困', '3113104956@qq.com', '13659499041', '', '5', '女', NULL, '', 0, '2026-03-26 18:07:14', '2026-05-09 18:23:15', '山东省', '泰安市', '岱岳区');
INSERT INTO `user` VALUES (3, 'lyh_0629', '$2a$10$gpPQW9UwrN9MccukZbBj.OtZ043Ph1Hh34AnmHdfmmChcpM2NCnDG', '使点什么坏', '2201380345@qq.com', '15315274963', '', '高级', '', NULL, '你们走不了了', 0, '2026-03-27 00:39:55', '2026-03-28 04:19:34', '', '', '');
INSERT INTO `user` VALUES (4, 'zyn_0717', '$2a$10$a8jcmjoPQ5/jX70gU4urPe8tC4MTtHIAzpYtDNP85x6qim05a5ow2', '澜白', '867038107@qq.com', '19931761637', '', '6', '女', '2004-07-17', '上哪找又打瓦又打羽毛球的', 0, '2026-03-27 00:48:57', '2026-05-09 18:22:50', '山东省', '泰安市', '岱岳区');
INSERT INTO `user` VALUES (5, 'gts_0316', '$2a$10$kovtEDUFLhJNRPJFMlgaNOwD8K0RV98TEwdyiVR6yzCbY6Q8SSlhi', '旅行鲨鲨', '1224059369@qq.com', '17659729065', NULL, NULL, NULL, NULL, NULL, 0, '2026-04-21 22:40:21', '2026-04-21 22:40:21', '', '', '');

-- ----------------------------
-- Table structure for venue
-- ----------------------------
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue`  (
  `vid` int NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场地编号',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `price_per_hour` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '每小时价格',
  `photos` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `owner_id` int NULL DEFAULT NULL,
  `status` int NULL DEFAULT 0,
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`vid`) USING BTREE,
  INDEX `fk_venue_merchant`(`owner_id` ASC) USING BTREE,
  CONSTRAINT `fk_venue_merchant` FOREIGN KEY (`owner_id`) REFERENCES `merchant` (`mid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of venue
-- ----------------------------
INSERT INTO `venue` VALUES (3, '01', '', 20.00, 'http://localhost:8080/uploads/0f222137-db00-4843-a9ba-f0da1940e890.png', 1, 0, '2026-03-31 05:38:51', '2026-03-31 06:53:47');
INSERT INTO `venue` VALUES (4, '02', '', 20.00, 'http://localhost:8080/uploads/6b2cf034-cf8a-4fcc-b62c-79a97f7b5495.png', 1, 0, '2026-03-31 05:39:47', '2026-03-31 05:39:47');
INSERT INTO `venue` VALUES (5, '03', '', 20.00, 'http://localhost:8080/uploads/90579967-3178-4fa4-b820-7d1ad045ba12.png', 1, 0, '2026-03-31 05:40:06', '2026-03-31 05:40:06');
INSERT INTO `venue` VALUES (6, '04', '', 20.00, 'http://localhost:8080/uploads/eee50dcc-bb09-4ad8-a755-77df7f8f295e.png', 1, 0, '2026-03-31 05:40:28', '2026-03-31 05:40:28');
INSERT INTO `venue` VALUES (7, '05', '独立场地', 50.00, 'http://localhost:8080/uploads/94191f75-49c6-4af4-8bd8-2330e6cd3507.png', 1, 0, '2026-03-31 05:40:58', '2026-03-31 05:40:58');
INSERT INTO `venue` VALUES (8, '1', '', 30.00, 'http://localhost:8080/uploads/742ba68c-38f4-44ee-90a4-a91a4bb2a990.png', 2, 0, '2026-03-31 07:52:10', '2026-03-31 07:52:10');
INSERT INTO `venue` VALUES (9, '2', '', 30.00, 'http://localhost:8080/uploads/c3304d7a-68cf-4152-bbcb-d1851d63631c.png', 2, 0, '2026-03-31 07:52:21', '2026-03-31 07:52:21');
INSERT INTO `venue` VALUES (10, '3', '有发球机', 60.00, 'http://localhost:8080/uploads/8b222539-b21d-4985-88f0-c554c252717a.png', 2, 0, '2026-03-31 07:52:44', '2026-03-31 07:52:44');

SET FOREIGN_KEY_CHECKS = 1;
