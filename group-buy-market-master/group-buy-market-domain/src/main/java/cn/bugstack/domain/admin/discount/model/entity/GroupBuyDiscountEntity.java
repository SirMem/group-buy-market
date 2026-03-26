package cn.bugstack.domain.admin.discount.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author needyou
 * @description 折扣实体
 * @create 2026-03-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyDiscountEntity {

    private Long id;
    private String discountId;
    private String discountName;
    private String discountDesc;
    private Integer discountType;
    private String marketPlan;
    private String marketExpr;
    private String tagId;
    private Date createTime;
    private Date updateTime;

}
