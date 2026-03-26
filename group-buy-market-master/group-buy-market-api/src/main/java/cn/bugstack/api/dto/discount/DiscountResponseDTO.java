package cn.bugstack.api.dto.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author needyou
 * @description 折扣返回对象
 * @create 2026-01-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResponseDTO {

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

