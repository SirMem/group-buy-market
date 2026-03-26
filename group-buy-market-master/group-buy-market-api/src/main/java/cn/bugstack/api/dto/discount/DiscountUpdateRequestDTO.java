package cn.bugstack.api.dto.discount;

import lombok.Data;

/**
 * @author needyou
 * @description 折扣更新请求
 * @create 2026-01-13
 */
@Data
public class DiscountUpdateRequestDTO {

    private String discountName;
    private String discountDesc;
    private Integer discountType;
    private String marketPlan;
    private String marketExpr;
    private String tagId;
}

