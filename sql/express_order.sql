/*
Navicat MySQL Data Transfer

Source Server         : spring_boot
Source Server Version : 50717
Source Host           : 192.168.129.133:3306
Source Database       : express_order

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-11-15 12:47:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order_evaluate
-- ----------------------------
DROP TABLE IF EXISTS `order_evaluate`;
CREATE TABLE `order_evaluate` (
  `id` varchar(128) NOT NULL COMMENT '订单ID',
  `has_open` tinyint(1) DEFAULT '0' COMMENT '评论是否开启（1：开启；0：关闭）',
  `user_id` varchar(128) NOT NULL DEFAULT '' COMMENT '用户ID',
  `user_score` decimal(6,3) DEFAULT NULL COMMENT '用户评分',
  `user_evaluate` varchar(255) NOT NULL DEFAULT '' COMMENT '用户评价',
  `user_date` datetime DEFAULT NULL COMMENT '用户评价时间',
  `courier_id` varchar(128) NOT NULL COMMENT '配送员ID',
  `courier_score` decimal(6,3) NOT NULL COMMENT '配送员评分',
  `courier_evaluate` varchar(255) NOT NULL DEFAULT '' COMMENT '配送员评价',
  `courier_date` datetime DEFAULT NULL COMMENT '配送员评价时间',
  `update_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- ----------------------------
-- Records of order_evaluate
-- ----------------------------
INSERT INTO `order_evaluate` VALUES ('1120376407025811458', '0', '1', '9.500', '满意', '2019-05-04 12:57:26', 'f10960e7392847a2c691ad066e2a87c4', '7.000', '可以的', '2019-05-04 12:57:12', '2019-05-04 12:57:26');
INSERT INTO `order_evaluate` VALUES ('1189426429004111873', '0', '270658f71ac110ec6cce0e08b1039b4d', '5.000', '一般般', '2019-10-30 15:11:53', 'f10960e7392847a2c691ad066e2a87c4', '9.500', 'ok', '2019-10-30 15:11:15', '2019-10-30 15:11:53');

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` varchar(128) NOT NULL COMMENT '订单ID',
  `user_id` varchar(128) DEFAULT NULL COMMENT '用户ID',
  `odd` varchar(128) DEFAULT NULL COMMENT '快递单号',
  `company` int(11) DEFAULT NULL COMMENT '快递公司',
  `rec_name` varchar(64) DEFAULT NULL COMMENT '收件人',
  `rec_tel` varchar(64) DEFAULT NULL COMMENT '收件短信',
  `rec_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `address` varchar(255) DEFAULT NULL COMMENT '快递寄达地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `courier_id` varchar(128) DEFAULT '' COMMENT '配送员ID',
  `type` tinyint(2) NOT NULL COMMENT '(0 - 送件上门， 1- 上门收件)',
  `status` int(11) DEFAULT NULL COMMENT '订单状态（1-等待节点 2-派送中 3-订单完成 4-订单异常 5-订单取消）',
  `courier_remark` varchar(255) DEFAULT NULL COMMENT '配送员备注',
  `has_delete` int(1) DEFAULT '0',
  `delete_type` int(1) DEFAULT '0' COMMENT '删除原因',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('1193196658343555074', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '1', '', '0', '0', '2019-11-10 00:00:25', '2019-11-09 23:38:19');
INSERT INTO `order_info` VALUES ('1193197911610040322', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '5', '', '0', '0', '2019-11-10 00:05:24', '2019-11-10 00:14:42');
INSERT INTO `order_info` VALUES ('1193198210416451586', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '5', '', '0', '0', '2019-11-10 00:06:36', '2019-11-10 00:15:50');
INSERT INTO `order_info` VALUES ('1193199083234332674', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '5', '', '0', '0', '2019-11-10 00:10:04', '2019-11-12 22:45:03');
INSERT INTO `order_info` VALUES ('1193199780537319426', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '5', '', '0', '0', '2019-11-10 00:12:50', '2019-11-12 22:45:03');
INSERT INTO `order_info` VALUES ('1193200298898862082', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '1', '', '0', '0', '2019-11-10 00:14:53', '2019-11-09 23:55:21');
INSERT INTO `order_info` VALUES ('1194281628936040450', '123', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12312', '', '0', '5', '', '0', '0', '2019-11-12 23:51:42', '2019-11-13 00:01:45');
INSERT INTO `order_info` VALUES ('1194283042055667713', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '123', '', '0', '1', '', '0', '0', '2019-11-12 23:57:19', '2019-11-13 00:09:16');
INSERT INTO `order_info` VALUES ('1194287699943497730', '123', '123123', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12312', '', '0', '1', '', '0', '0', '2019-11-13 00:15:49', '2019-11-13 00:16:46');
INSERT INTO `order_info` VALUES ('1194288125078151169', '123', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '123', '', '0', '1', '', '0', '0', '2019-11-13 00:17:32', '2019-11-13 00:18:06');
INSERT INTO `order_info` VALUES ('1194288586585808897', '123', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '23234', '', '0', '5', '', '0', '0', '2019-11-13 00:19:22', '2019-11-13 11:23:17');
INSERT INTO `order_info` VALUES ('1194478839955247105', '123', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '232342', '', '0', '1', '', '0', '0', '2019-11-13 12:55:21', '2019-11-13 02:38:04');
INSERT INTO `order_info` VALUES ('1194516441534042114', '12345', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12312', '', '0', '1', '', '0', '0', '2019-11-13 15:24:46', '2019-11-13 05:19:35');
INSERT INTO `order_info` VALUES ('1195008227285155841', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1', '', '0', '1', '', '0', '0', '2019-11-14 23:58:56', '2019-11-13 23:23:11');
INSERT INTO `order_info` VALUES ('1195150737500340226', '123', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12', '', '0', '1', '', '0', '0', '2019-11-15 09:25:15', '2019-11-13 23:39:10');

-- ----------------------------
-- Table structure for order_payment
-- ----------------------------
DROP TABLE IF EXISTS `order_payment`;
CREATE TABLE `order_payment` (
  `order_id` varchar(128) CHARACTER SET utf8 NOT NULL COMMENT '订单ID',
  `status` int(11) DEFAULT NULL COMMENT '支付状态',
  `type` int(11) DEFAULT NULL COMMENT '支付方式',
  `payment` decimal(10,2) DEFAULT NULL COMMENT '付款金额',
  `payment_id` varchar(255) CHARACTER SET utf8 DEFAULT '' COMMENT '支付流水号',
  `seller` varchar(255) CHARACTER SET utf8 DEFAULT '' COMMENT '收款方',
  `remark` varchar(255) CHARACTER SET utf8 DEFAULT '' COMMENT '备注',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `fk_payment_type` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

-- ----------------------------
-- Records of order_payment
-- ----------------------------
INSERT INTO `order_payment` VALUES ('1193196658343555074', '3', '1', '4.00', '2019111022001435601000133163', '2088102179254465', '', '2019-11-10 00:00:25', '2019-11-09 23:38:19');
INSERT INTO `order_payment` VALUES ('1193197911610040322', '2', '1', '4.00', '', '2088102179254465', '', '2019-11-10 00:05:24', '2019-11-10 00:14:41');
INSERT INTO `order_payment` VALUES ('1193198210416451586', '2', '1', '4.00', '', '2088102179254465', '', '2019-11-10 00:06:36', '2019-11-10 00:15:50');
INSERT INTO `order_payment` VALUES ('1193199083234332674', '2', '1', '4.00', '', '2088102179254465', '', '2019-11-10 00:10:04', '2019-11-12 22:45:03');
INSERT INTO `order_payment` VALUES ('1193199780537319426', '2', '1', '4.00', '', '2088102179254465', '', '2019-11-10 00:12:50', '2019-11-12 22:45:03');
INSERT INTO `order_payment` VALUES ('1193200298898862082', '3', '1', '4.00', '2019111022001435601000133164', '2088102179254465', '', '2019-11-10 00:14:53', '2019-11-09 23:55:21');
INSERT INTO `order_payment` VALUES ('1194281628936040450', '2', '1', '3.60', '', '2088102179254465', '', '2019-11-12 23:51:42', '2019-11-13 00:01:45');
INSERT INTO `order_payment` VALUES ('1194283042055667713', '3', '1', '48.00', '2019111322001435601000141490', '2088102179254465', '', '2019-11-12 23:57:19', '2019-11-13 00:09:16');
INSERT INTO `order_payment` VALUES ('1194287699943497730', '3', '1', '43.20', '2019111322001435601000141491', '2088102179254465', '', '2019-11-13 00:15:49', '2019-11-13 00:16:46');
INSERT INTO `order_payment` VALUES ('1194288125078151169', '3', '1', '4442.40', '2019111322001435601000142568', '2088102179254465', '', '2019-11-13 00:17:32', '2019-11-13 00:18:06');
INSERT INTO `order_payment` VALUES ('1194288586585808897', '2', '1', '4442.40', '', '2088102179254465', '', '2019-11-13 00:19:22', '2019-11-13 11:23:17');
INSERT INTO `order_payment` VALUES ('1194478839955247105', '3', '1', '43.20', '2019111322001435601000142569', '2088102179254465', '', '2019-11-13 12:55:21', '2019-11-13 02:38:04');
INSERT INTO `order_payment` VALUES ('1194516441534042114', '3', '1', '38.40', '2019111322001435601000141492', '2088102179254465', '', '2019-11-13 15:24:46', '2019-11-13 05:19:35');
INSERT INTO `order_payment` VALUES ('1195008227285155841', '3', '1', '48.00', '2019111422001435601000144311', '2088102179254465', '', '2019-11-14 23:58:56', '2019-11-13 23:23:11');
INSERT INTO `order_payment` VALUES ('1195150737500340226', '3', '1', '442.80', '2019111522001435601000145615', '2088102179254465', '', '2019-11-15 09:25:15', '2019-11-13 23:39:10');
