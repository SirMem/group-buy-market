package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author needyou
 * @description 渠道商品活动绑定实体
 * @create 2026-03-12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCSkuActivityEntity {

    private Long id;
    private String source;
    private String channel;
    private Long activityId;
    private String goodsId;
    private Date createTime;
    private Date updateTime;

}
