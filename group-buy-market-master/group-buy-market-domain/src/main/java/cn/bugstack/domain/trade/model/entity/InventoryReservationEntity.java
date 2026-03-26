package cn.bugstack.domain.trade.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 外部交易单号，用于幂等性
     */
    private String outTradeNo;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 团ID(可选)
     */
    private String teamId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 商品ID
     */
    private String goodsId;

    /**
     * 状态: 0-RESERVED, 1-CONSUMED, 2-RELEASED
     */
    private Integer status; // 也可以使用枚举类型，见下方推荐

    /**
     * 过期时间(支付超时/预留过期)
     */
    private LocalDateTime expireAt;

    /**
     * 扩展字段(JSON)
     */
    private String ext;
}
