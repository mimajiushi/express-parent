/*
Navicat MySQL Data Transfer

Source Server         : spring_boot
Source Server Version : 50717
Source Host           : 192.168.129.133:3306
Source Database       : express_order

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-11-26 16:10:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for order_evaluate
-- ----------------------------
DROP TABLE IF EXISTS `order_evaluate`;
CREATE TABLE `order_evaluate` (
  `id` varchar(128) NOT NULL COMMENT '订单ID',
  `has_open` tinyint(1) NOT NULL DEFAULT '0' COMMENT '评论是否开启（1：开启；0：关闭）',
  `user_id` varchar(128) NOT NULL DEFAULT '' COMMENT '用户ID',
  `user_score` decimal(6,3) NOT NULL DEFAULT '-1.000' COMMENT '用户评分',
  `user_evaluate` varchar(255) NOT NULL DEFAULT '' COMMENT '用户评价',
  `user_date` datetime DEFAULT '1111-11-11 11:11:11' COMMENT '用户评价时间',
  `courier_id` varchar(128) NOT NULL DEFAULT '' COMMENT '配送员ID',
  `courier_score` decimal(6,3) NOT NULL DEFAULT '-1.000' COMMENT '配送员评分',
  `courier_evaluate` varchar(255) NOT NULL DEFAULT '' COMMENT '配送员评价',
  `courier_date` datetime DEFAULT '1111-11-11 11:11:11' COMMENT '配送员评价时间',
  `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `courier_id` (`courier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单评价表';

-- ----------------------------
-- Records of order_evaluate
-- ----------------------------
INSERT INTO `order_evaluate` VALUES ('1196063168418344962', '0', '270658f71ac110ec6cce0e08b1039b4d', '10.000', 'asasda', '2019-11-26 16:02:45', '63454354', '5.000', 'sefsefsf', '2019-11-26 16:09:45', '2019-11-18 20:01:45');
INSERT INTO `order_evaluate` VALUES ('1197069990839205889', '0', '270658f71ac110ec6cce0e08b1039b4d', '9.500', 'zsczsczscz', '2019-11-26 16:03:02', '63454354', '9.500', 'sefsef', '2019-11-26 16:09:48', '2019-11-18 20:02:03');
INSERT INTO `order_evaluate` VALUES ('1197093822861484034', '0', '270658f71ac110ec6cce0e08b1039b4d', '5.500', 'sefsefsefsef', '2019-11-26 16:03:10', '63454354', '1.000', 'sefsefs', '2019-11-26 16:09:51', '2019-11-18 20:02:12');

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
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `courier_id` (`courier_id`),
  KEY `create_date` (`create_date`),
  KEY `update_date` (`update_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('1196053966312538114', '270658f71ac110ec6cce0e08b1039b4d', '123', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '用户备注', '', '0', '5', '配送员备注', '0', '0', '2019-11-17 21:14:20', '2019-11-16 02:31:22');
INSERT INTO `order_info` VALUES ('1196063168418344962', '270658f71ac110ec6cce0e08b1039b4d', '45345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '用户备注', '63454354', '0', '3', '订单完成', '0', '0', '2019-11-17 21:50:54', '2019-11-16 18:02:00');
INSERT INTO `order_info` VALUES ('1197069990839205889', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '123', '63454354', '0', '3', '订单完成', '0', '0', '2019-11-20 16:31:40', '2019-11-16 18:18:29');
INSERT INTO `order_info` VALUES ('1197093822861484034', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '123', '63454354', '0', '3', '完成订单', '0', '0', '2019-11-20 18:06:22', '2019-11-16 18:20:49');
INSERT INTO `order_info` VALUES ('1197094257487847425', '270658f71ac110ec6cce0e08b1039b4d', '123', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '123', '', '0', '1', '', '0', '0', '2019-11-20 18:08:06', '2019-11-20 18:08:06');
INSERT INTO `order_info` VALUES ('1197778167247441921', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '107', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1231', '57546432', '0', '2', '完成', '0', '0', '2019-11-22 15:25:42', '2019-11-22 20:36:47');
INSERT INTO `order_info` VALUES ('1197779106989641729', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12312', '57546432', '0', '6', '', '0', '0', '2019-11-22 15:29:27', '2019-11-22 15:32:21');
INSERT INTO `order_info` VALUES ('1197781112387047425', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345123', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12', '', '0', '5', '', '0', '0', '2019-11-22 15:37:25', '2019-11-22 15:46:41');
INSERT INTO `order_info` VALUES ('1197863229603221505', '270658f71ac110ec6cce0e08b1039b4d', '123', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '12312', '63454354', '0', '6', '', '0', '0', '2019-11-22 21:03:43', '2019-11-22 21:04:25');
INSERT INTO `order_info` VALUES ('1198565772092293122', '270658f71ac110ec6cce0e08b1039b4d', '3453434323f3f3g3g', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1351', '12312523', '0', '2', '揽收完成', '0', '0', '2019-11-24 19:35:21', '2019-11-24 19:38:12');
INSERT INTO `order_info` VALUES ('1198575791714123778', '270658f71ac110ec6cce0e08b1039b4d', '3453453534345345', '101', 'chen wenjie', '15920323196', 'huaruan\r\nchina', 'huaruan\r\nchina', '1231', '23542345', '0', '4', '订单异常', '0', '0', '2019-11-24 20:15:11', '2019-11-24 20:26:35');

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
INSERT INTO `order_payment` VALUES ('1197069990839205889', '3', '1', '48.00', '2019112022001435601000164109', '2088102179254465', '', '2019-11-20 16:31:40', '2019-11-16 16:32:40');
INSERT INTO `order_payment` VALUES ('1197093822861484034', '3', '1', '48.00', '2019112022001435601000165946', '2088102179254465', '', '2019-11-20 18:06:22', '2019-11-16 18:15:18');
INSERT INTO `order_payment` VALUES ('1197094257487847425', '1', '1', '4.00', '', '2088102179254465', '', '2019-11-20 18:08:06', '2019-11-20 18:08:06');
INSERT INTO `order_payment` VALUES ('1197778167247441921', '3', '1', '48.00', '2019112222001435601000167269', '2088102179254465', '', '2019-11-22 15:25:42', '2019-11-17 14:01:40');
INSERT INTO `order_payment` VALUES ('1197779106989641729', '3', '1', '48.00', '2019112222001435601000168652', '2088102179254465', '', '2019-11-22 15:29:27', '2019-11-17 14:06:35');
INSERT INTO `order_payment` VALUES ('1197781112387047425', '2', '1', '4.00', '', '2088102179254465', '', '2019-11-22 15:37:25', '2019-11-22 15:46:41');
INSERT INTO `order_payment` VALUES ('1197863229603221505', '3', '1', '4.00', '2019112222001435601000168653', '2088102179254465', '', '2019-11-22 21:03:43', '2019-11-17 20:06:23');
INSERT INTO `order_payment` VALUES ('1198565772092293122', '3', '1', '2.00', '2019112422001435601000170140', '2088102179254465', '', '2019-11-24 19:35:21', '2019-11-18 07:00:26');
INSERT INTO `order_payment` VALUES ('1198575791714123778', '3', '1', '4.00', '2019112422001435601000171140', '2088102179254465', '', '2019-11-24 20:15:11', '2019-11-18 07:43:28');
