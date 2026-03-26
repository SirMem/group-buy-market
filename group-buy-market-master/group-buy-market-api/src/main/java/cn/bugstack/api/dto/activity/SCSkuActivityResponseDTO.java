package cn.bugstack.api.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author needyou
 * @description 渠道商品活动绑定返回对象
 * @create 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCSkuActivityResponseDTO {

    private Long id;
    private String source;
    private String channel;
    private Long activityId;
    private String goodsId;
    private Date createTime;
    private Date updateTime;
}

