package cn.bugstack.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityInventory {

    private Long id;

    private Long activityId;

    private String goodsId;

    private Long totalStock;

    private Long reservedStock;

    private Long soldStock;

    private String version;

    private Date createTime;

    private Date updateTime;
}
