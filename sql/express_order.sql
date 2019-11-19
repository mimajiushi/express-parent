/*
Navicat MySQL Data Transfer

Source Server         : spring_boot
Source Server Version : 50717
Source Host           : 192.168.129.133:3306
Source Database       : express_order

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-11-19 14:30:24
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
  `update_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- ----------------------------
-- Records of order_evaluate
-- ----------------------------
INSERT INTO `order_evaluate` VALUES ('1120376407025811458', '0', '1', '9.500', '满意', '2019-05-04 12:57:26', 'f10960e7392847a2c691ad066e2a87c4', '7.000', '可以的', '2019-05-04 12:57:12', '2019-05-04 12:57:26');
INSERT INTO `order_evaluate` VALUES ('1189426429004111873', '0', '270658f71ac110ec6cce0e08b1039b4d', '5.000', '一般般', '2019-10-30 15:11:53', 'f10960e7392847a2c691ad066e2a87c4', '9.500', 'ok', '2019-10-30 15:11:15', '2019-10-30 15:11:53');
INSERT INTO `order_evaluate` VALUES ('1189426429004111874', '0', '270658f71ac110ec6cce0e08b1039b4d', '5.000', '一般般', '2019-10-30 15:11:53', 'f10960e7392847a2c691ad066e2a87c4', '9.500', 'ok', '2019-10-30 15:11:15', '2019-10-30 15:11:53');
INSERT INTO `order_evaluate` VALUES ('1189426429004111875', '0', '270658f71ac110ec6cce0e08b1039b4d', '5.000', '一般般', '2019-10-30 15:11:53', 'f10960e7392847a2c691ad066e2a87c4', '9.500', 'ok', '2019-10-30 15:11:15', '2019-10-30 15:11:53');
INSERT INTO `order_evaluate` VALUES ('1189426429004111876', '0', '270658f71ac110ec6cce0e08b1039b4d', '5.000', '一般般', '2019-10-30 15:11:53', 'f10960e7392847a2c691ad066e2a87c4', '9.500', 'ok', '2019-10-30 15:11:15', '2019-10-30 15:11:53');

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` varchar(128) NOT NULL COMMENT '订单ID',
  `user_id` varchar(128) DEFAULT NULL COMMENT '用户ID',
  `odd` varchar(128) DEFAULT '' COMMENT '快递单号',
  `company` int(11) DEFAULT NULL COMMENT '快递公司',
  `rec_name` varchar(64) DEFAULT NULL COMMENT '收件人',
  `rec_tel` varchar(64) DEFAULT NULL COMMENT '收件短信',
  `rec_address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `address` varchar(255) DEFAULT NULL COMMENT '快递寄达地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `courier_id` varchar(128) DEFAULT '' COMMENT '配送员ID',
  `type` tinyint(2) NOT NULL COMMENT '(0 - 送件上门， 1- 上门收件)',
  `status` int(11) DEFAULT NULL COMMENT '订单状态（1-等待节点 2-派送中 3-订单完成 4-订单异常 5-订单取消 6-等待揽收）',
  `courier_remark` varchar(255) DEFAULT NULL COMMENT '配送员备注',
  `has_delete` int(1) DEFAULT '0',
  `delete_type` int(1) DEFAULT '0' COMMENT '删除原因',
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('1196053966312538114', '270658f71ac110ec6cce0e08b1039b4d', '123', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '用户备注', '', '0', '5', '配送员备注', '0', '0', '2019-11-17 21:14:20', '2019-11-16 02:31:22');
INSERT INTO `order_info` VALUES ('1196063168418344962', '270658f71ac110ec6cce0e08b1039b4d', '45345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '用户备注', '123456', '0', '6', '配送员备注', '0', '0', '2019-11-17 21:50:54', '2019-11-16 02:31:27');

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
  `create_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `fk_payment_type` (`type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

-- ----------------------------
-- Records of order_payment
-- ----------------------------
INSERT INTO `order_payment` VALUES ('1196053966312538114', '2', '1', '4.00', '', '2088102179254465', '', '2019-11-17 21:14:20', '2019-11-17 21:23:40');
INSERT INTO `order_payment` VALUES ('1196063168418344962', '3', '1', '4.00', '2019111722001435601000157583', '2088102179254465', '', '2019-11-17 21:50:54', '2019-11-15 15:27:29');
