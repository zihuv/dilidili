/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云-MySQL
 Source Server Type    : MySQL
 Source Server Version : 80013 (8.0.13)
 Source Host           : zihuv7.rwlb.rds.aliyuncs.com:3306
 Source Schema         : dilidili

 Target Server Type    : MySQL
 Target Server Version : 80013 (8.0.13)
 File Encoding         : 65001

 Date: 24/03/2024 19:50:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_video
-- ----------------------------
DROP TABLE IF EXISTS `tb_video`;
CREATE TABLE `tb_video`  (
  `id` bigint(20) NOT NULL,
  `video_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL,
  `video_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL,
  `video_path` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL,
  `pic_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '视频作者的 id',
  `play_amount` int(11) NULL DEFAULT 0,
  `like_amount` int(11) NULL DEFAULT NULL,
  `comment_amount` int(11) NULL DEFAULT 0,
  `collect_amount` int(11) NULL DEFAULT NULL,
  `share_amount` int(11) NULL DEFAULT NULL,
  `is_reviewed` tinyint(1) NULL DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `del_flag` tinyint(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_video
-- ----------------------------
INSERT INTO `tb_video` VALUES (1749763618125733888, '谷粒商城', 'guliguli-1706011117858.mp4', 'http://s6znc94v3.hn-bkt.clouddn.com/guliguli-1706011117858.mp4?e=1706014729&token=Gg9VIm5qU7TpmnX7GryM1wtb4h4H9vS7LKGZllq-:0s0Qyh3oBqlHzgbAaGpcb-JJZIk=', NULL, 1748664664352997376, 1, 3, 1000000, 100000, 100000, 0, '2024-02-08 20:03:53', '2024-01-23 19:58:50', 0);
INSERT INTO `tb_video` VALUES (1750505486300037120, 'bbbb', 'gulimall-1706187980271.mp4', 'http://s6znc94v3.hn-bkt.clouddn.com/gulimall-1706187980271.mp4?e=1706191605&token=Gg9VIm5qU7TpmnX7GryM1wtb4h4H9vS7LKGZllq-:nNp6P8fV46dDjEaS-XsTlMrWTK0=', NULL, 1748664664352997376, 0, 0, 0, 0, 0, 0, '2024-02-03 15:08:48', '2024-01-25 21:06:45', 0);

-- ----------------------------
-- Table structure for tb_like
-- ----------------------------
DROP TABLE IF EXISTS `tb_like`;
CREATE TABLE `tb_like`  (
  `id` bigint(20) NOT NULL,
  `video_id` bigint(20) NULL DEFAULT NULL,
  `like_user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_like
-- ----------------------------

-- ----------------------------
-- Table structure for tb_friend
-- ----------------------------
DROP TABLE IF EXISTS `tb_friend`;
CREATE TABLE `tb_friend`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `friend_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_friend
-- ----------------------------
INSERT INTO `tb_friend` VALUES (1755420690582441984, 1748664664352997376, 1748683165054636032);
INSERT INTO `tb_friend` VALUES (1755420690733436928, 1748683165054636032, 1748664664352997376);

-- ----------------------------
-- Table structure for tb_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_follow`;
CREATE TABLE `tb_user_follow`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `follow_user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user_follow
-- ----------------------------
INSERT INTO `tb_user_follow` VALUES (1748727628321886208, 1748664664352997376, 1748683165054636032);
INSERT INTO `tb_user_follow` VALUES (1748884063198392320, 1748664664352997376, 1748683180816830464);
INSERT INTO `tb_user_follow` VALUES (1748893074241130496, 1748683165054636032, 1748664664352997376);
INSERT INTO `tb_user_follow` VALUES (1752293512625786880, 1748664664352997376, 1748683196486750208);

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL,
  `follow_amount` int(11) NULL DEFAULT NULL,
  `fans_amount` int(11) NULL DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `del_flag` tinyint(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1748664664352997376, 'admin', '123456', 0, 0, '2024-01-20 20:26:13', '2024-01-20 19:11:59', 0);
INSERT INTO `tb_user` VALUES (1748683165054636032, 'userA', '123456', 0, 0, '2024-01-20 20:26:13', '2024-01-20 20:25:30', 0);
INSERT INTO `tb_user` VALUES (1748683180816830464, 'userB', '123456', 0, 0, '2024-01-20 20:26:13', '2024-01-20 20:25:34', 0);
INSERT INTO `tb_user` VALUES (1748683196486750208, 'userC', '123456', 0, 0, '2024-01-20 20:26:13', '2024-01-20 20:25:38', 0);
INSERT INTO `tb_user` VALUES (1748683212949393408, 'userD', '123456', 0, 0, '2024-01-20 20:26:13', '2024-01-20 20:25:42', 0);

-- ----------------------------
-- Table structure for tb_notification
-- ----------------------------
DROP TABLE IF EXISTS `tb_notification`;
CREATE TABLE `tb_notification`  (
  `id` bigint(20) NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `reply_user_id` bigint(20) NULL DEFAULT NULL,
  `original_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL,
  `reply_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL,
  `is_read` tinyint(4) NULL DEFAULT 0
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_notification
-- ----------------------------
INSERT INTO `tb_notification` VALUES (1753680582766235648, 1748683165054636032, 1748664664352997376, '我是父评论', '', 0);

-- ----------------------------
-- Table structure for tb_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_message`;
CREATE TABLE `tb_message`  (
  `id` bigint(20) NOT NULL,
  `destination` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT '消息发送目的地',
  `message_body` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NOT NULL COMMENT '消息体',
  `send_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '消息发送状态',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `del_flag` tinyint(4) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci COMMENT = '本地消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_message
-- ----------------------------
INSERT INTO `tb_message` VALUES (1753334255607996416, 'relay_comment_topic_key:relay_comment_tag_key', '{\"payload\":{\"message\":{\"userId\":1748664664352997376,\"replyUserId\":0,\"originalComment\":\"我是父评论\",\"replyComment\":\"\"},\"uuid\":\"7b68378d-fd68-41a9-bf21-2cb03b5be7fb\",\"timestamp\":1706862436565},\"headers\":{\"KEYS\":\"1706862436564\",\"id\":\"9eeaf301-58d8-b033-053e-ae032c5841b2\",\"timestamp\":1706862436567}}', 0, '2024-02-02 16:27:17', '2024-02-02 16:27:17', 0);
INSERT INTO `tb_message` VALUES (1753680581977706496, 'relay_comment_topic_key:relay_comment_tag_key', '{\"payload\":{\"message\":{\"userId\":1748683165054636032,\"replyUserId\":1748664664352997376,\"originalComment\":\"我是父评论\",\"replyComment\":\"\"},\"uuid\":\"d371da60-25a9-4065-8e00-8de83ed7c912\",\"timestamp\":1706945007206},\"headers\":{\"KEYS\":\"1706945007205\",\"id\":\"77154126-64fd-185b-29b6-13548fe2bad2\",\"timestamp\":1706945007209}}', 0, '2024-02-03 15:23:27', '2024-02-03 15:23:27', 0);

-- ----------------------------
-- Table structure for tb_comment
-- ----------------------------
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment`  (
  `id` bigint(20) NOT NULL,
  `root_id` bigint(20) NULL DEFAULT 0,
  `parent_id` bigint(20) NULL DEFAULT NULL,
  `video_id` bigint(20) NULL DEFAULT NULL,
  `content_author_id` bigint(20) NULL DEFAULT NULL,
  `to_user_id` bigint(20) NULL DEFAULT 0 COMMENT '被回复评论的用户 id',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL,
  `like_num` bigint(20) NULL DEFAULT NULL,
  `reply_num` bigint(20) NULL DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `del_flag` tinyint(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_comment
-- ----------------------------
INSERT INTO `tb_comment` VALUES (1753334255431835648, 0, 0, 1749763618125733888, 1748664664352997376, 0, '我是父评论', 0, 0, '2024-02-02 16:27:17', '2024-02-02 16:27:17', 0);
INSERT INTO `tb_comment` VALUES (1753679156098895872, 0, 0, 1749763618125733888, 1748664664352997376, 0, '我是父评论', 0, 0, '2024-02-03 15:17:47', '2024-02-03 15:17:47', 0);
INSERT INTO `tb_comment` VALUES (1753680581759602688, 0, 0, 1749763618125733888, 1748683165054636032, 0, '我是父评论', 0, 0, '2024-02-03 15:23:27', '2024-02-03 15:23:27', 0);

-- ----------------------------
-- Table structure for tb_collect_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_collect_relation`;
CREATE TABLE `tb_collect_relation`  (
  `id` bigint(20) NOT NULL,
  `collect_id` bigint(20) NULL DEFAULT NULL,
  `video_id` bigint(20) NULL DEFAULT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_collect_relation
-- ----------------------------
INSERT INTO `tb_collect_relation` VALUES (1752322878114127872, 1752320957840187392, 1749763618125733888, 1748664664352997376);

-- ----------------------------
-- Table structure for tb_collect
-- ----------------------------
DROP TABLE IF EXISTS `tb_collect`;
CREATE TABLE `tb_collect`  (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NULL DEFAULT NULL,
  `collect_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL DEFAULT 'DEFAULT',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `del_flag` tinyint(4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_collect
-- ----------------------------
INSERT INTO `tb_collect` VALUES (1752320957840187392, 1748664664352997376, 'ABC', '2024-01-30 21:20:48', '2024-01-30 21:20:48', 0);

-- ----------------------------
-- Table structure for tb_classification_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_classification_relation`;
CREATE TABLE `tb_classification_relation`  (
  `id` bigint(20) NOT NULL,
  `classification_id` bigint(20) NULL DEFAULT NULL,
  `vedio_id` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_classification_relation
-- ----------------------------

-- ----------------------------
-- Table structure for tb_classification
-- ----------------------------
DROP TABLE IF EXISTS `tb_classification`;
CREATE TABLE `tb_classification`  (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_icelandic_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_icelandic_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_classification
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
