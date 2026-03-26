package cn.bugstack.api.dto.activity;

import lombok.Data;

/**
 * @author needyou
 * @description 渠道商品活动绑定请求
 * @create 2026-01-13
 */
@Data
public class SCSkuActivityBindRequestDTO {

    private String source;
    private String channel;
    private String goodsId;
    private Long activityId;
}

