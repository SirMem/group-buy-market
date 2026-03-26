package cn.bugstack.infrastructure.persistent.po.join;

import cn.bugstack.infrastructure.persistent.po.Sku;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkuWithInventoryPO extends Sku {

    private Long activityId;

    private String goodsId;

    private Long totalStock;

    private Long reservedStock;

    private Long soldStock;
}
