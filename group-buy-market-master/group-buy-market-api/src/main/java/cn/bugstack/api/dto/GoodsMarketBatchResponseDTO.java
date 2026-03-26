package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author needyou
 * @description 批量商品营销应答对象
 * @create 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsMarketBatchResponseDTO {

    /** 查询成功的营销配置列表 */
    private List<GoodsMarketResponseDTO> goodsMarketList;

    /** 无营销配置/异常的商品ID列表 */
    private List<String> invalidGoodsIdList;
}

