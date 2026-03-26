package cn.bugstack.api.dto.discount;

import lombok.Data;

/**
 * @author needyou
 * @description 折扣创建请求
 * @create 2026-01-13
 */
@Data
public class DiscountCreateRequestDTO {

    /** 折扣ID（可选，不传则后端生成8位数字） */
    private String discountId;

    private String discountName;
    private String discountDesc;
    /** 折扣类型（0:base、1:tag） */
    private Integer discountType;
    /** 营销优惠计划（ZJ/MJ/N） */
    private String marketPlan;
    /** 营销优惠表达式 */
    private String marketExpr;
    /** 人群标签（可空） */
    private String tagId;
}

