package cn.bugstack.domain.trade.model.entity;

import java.math.BigDecimal;


/**
 * @description 活动-商品库存聚合
 */
public class ActivityInventoryEntity {

    private String activityId;
    private String goodsId;
    private BigDecimal totalStock;
    private BigDecimal reserved;
    private BigDecimal sold;
}
