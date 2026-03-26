package cn.bugstack.api;

import cn.bugstack.api.dto.GoodsMarketBatchRequestDTO;
import cn.bugstack.api.dto.GoodsMarketBatchResponseDTO;
import cn.bugstack.api.dto.GoodsMarketRequestDTO;
import cn.bugstack.api.dto.GoodsMarketResponseDTO;
import cn.bugstack.api.dto.MarketGoodsCardDTO;
import cn.bugstack.api.dto.MarketGoodsPageRequestDTO;
import cn.bugstack.api.response.Response;
import com.github.pagehelper.PageInfo;

/**
 * @author needyou
 * @description 营销首页服务接口
 * @create 2025-02-02 16:02
 */
public interface IMarketIndexService {

    /**
     * 查询拼团营销配置
     *
     * @param goodsMarketRequestDTO 营销商品信息
     * @return 营销配置信息
     */
    Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(GoodsMarketRequestDTO goodsMarketRequestDTO);

    /**
     * 批量查询拼团营销配置（用于商品 Index 列表聚合展示）
     *
     * @param goodsMarketBatchRequestDTO 批量营销商品信息
     * @return 批量营销配置信息
     */
    Response<GoodsMarketBatchResponseDTO> queryGroupBuyMarketConfigBatch(GoodsMarketBatchRequestDTO goodsMarketBatchRequestDTO);

    /**
     * 用户侧 Index 聚合分页查询：SKU 分页 + 营销试算 +（可选）库存/团队统计
     */
    Response<PageInfo<MarketGoodsCardDTO>> queryMarketGoodsPage(MarketGoodsPageRequestDTO requestDTO);

}
