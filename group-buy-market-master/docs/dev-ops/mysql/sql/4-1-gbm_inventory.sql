/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
CREATE database if NOT EXISTS `group_buy_market` default character set utf8mb4 collate utf8mb4_0900_ai_ci;
use `group_buy_market`;
-- 1) 活动-商品库存表（总库存）
DROP TABLE IF EXISTS `activity_inventory`;
CREATE TABLE `activity_inventory` (
                                      `id`            bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                      `activity_id`   bigint(8)  NOT NULL COMMENT '活动ID',
                                      `goods_id`      varchar(16) NOT NULL COMMENT '商品ID',
                                      `total_stock`   int(11)    NOT NULL COMMENT '总库存(可售总份数)',
                                      `reserved_stock` int(11)   NOT NULL DEFAULT 0 COMMENT '已预留(锁单未支付)',
                                      `sold_stock`    int(11)    NOT NULL DEFAULT 0 COMMENT '已售出(支付成功)',
                                      `version`       int(11)    NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
                                      `create_time`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `update_time`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                      PRIMARY KEY (`id`),
                                      UNIQUE KEY `uq_activity_goods` (`activity_id`,`goods_id`)
    -- MySQL 8.0.16+ 可开启 CHECK 约束；低版本仅做文档约束
    -- , CHECK (`total_stock` >= 0 AND `reserved_stock` >= 0 AND `sold_stock` >= 0)
    -- , CHECK (`total_stock` >= `reserved_stock` + `sold_stock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动-商品总库存';

-- 2) 库存预留流水表（预占 -> 确认/释放）
DROP TABLE IF EXISTS `inventory_reservation`;
CREATE TABLE `inventory_reservation` (
                                         `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                         `out_trade_no`       varchar(64)  NOT NULL COMMENT '业务订单ID/外部交易单号',
                                         `user_id`        varchar(64)  NOT NULL COMMENT '用户ID',
                                         `team_id`        varchar(16)           DEFAULT NULL COMMENT '团ID(可选)',
                                         `activity_id`    bigint(8)     NOT NULL COMMENT '活动ID',
                                         `goods_id`       varchar(16)   NOT NULL COMMENT '商品ID',
                                         `status`         tinyint(1)    NOT NULL COMMENT '状态: 0-RESERVED, 1-CONSUMED, 2-RELEASED',
                                         `expire_at`      datetime      NOT NULL COMMENT '过期时间(支付超时/预留过期)',
                                         `ext`            varchar(256)           DEFAULT NULL COMMENT '扩展字段(JSON)',
                                         `create_time`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `update_time`    datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uq_outTradeNo_id` (`out_trade_no`),
                                         KEY `idx_expire_at` (`expire_at`),
                                         KEY `idx_activity_goods_status` (`activity_id`, `goods_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='总库存预留记录';

-- 3) 库存调整日志（初始化/人工调账/自动补回）
DROP TABLE IF EXISTS `inventory_adjust_log`;
CREATE TABLE `inventory_adjust_log` (
                                        `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
                                        `activity_id`  bigint(8)    NOT NULL COMMENT '活动ID',
                                        `goods_id`     varchar(16)  NOT NULL COMMENT '商品ID',
                                        `adjust_type`  tinyint(1)   NOT NULL COMMENT '类型: 0-INIT, 1-INCR, 2-DECR, 3-ADMIN_FIX, 4-REFUND_RELEASE',
                                        `delta`        int(11)      NOT NULL COMMENT '变更量(正负皆可, 对total或可用量视实现)',
                                        `operator`     varchar(64)  NOT NULL COMMENT '操作人/系统',
                                        `remark`       varchar(256)          DEFAULT NULL COMMENT '备注',
                                        `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_activity_goods` (`activity_id`,`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='总库存调整日志';

-- 4) 可选：库存总览视图（便于排查/报表）
DROP VIEW IF EXISTS `v_activity_inventory_summary`;
CREATE VIEW `v_activity_inventory_summary` AS
SELECT
    ai.activity_id,
    ai.goods_id,
    ai.total_stock,
    ai.reserved_stock,
    ai.sold_stock,
    (ai.total_stock - ai.reserved_stock - ai.sold_stock) AS available_stock,
    ai.version,
    ai.update_time
FROM activity_inventory ai;

-- 5) 示例数据：为现有活动100123+商品9890001初始化500份库存
-- 请根据实际活动/商品替换ID
INSERT INTO `activity_inventory` (`activity_id`,`goods_id`,`total_stock`,`reserved_stock`,`sold_stock`,`version`)
VALUES (100123,'9890001',500,0,0,0);

INSERT INTO `inventory_adjust_log`
(`activity_id`,`goods_id`,`adjust_type`,`delta`,`operator`,`remark`)
VALUES (100123,'9890001',0,500,'system','初始化库存');


CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                        `username` VARCHAR(50) NOT NULL COMMENT '用户名',
                        `password` VARCHAR(255) NOT NULL COMMENT '密码',
                        `role` VARCHAR(50) DEFAULT NULL COMMENT '角色',
                        `user_status` TINYINT DEFAULT 1 COMMENT '状态 (例如：1-启用, 0-禁用)',
                        `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `uk_username` (`username`) -- 通常用户名需要唯一索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
-- 回滚开关
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
