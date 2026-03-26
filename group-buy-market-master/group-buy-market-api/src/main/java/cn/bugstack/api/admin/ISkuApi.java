package cn.bugstack.api.admin;

import cn.bugstack.api.dto.CreateSkuRequestDTO;
import cn.bugstack.api.dto.SkuInventoryPageReq;
import cn.bugstack.api.dto.SkuResponseDTO;
import cn.bugstack.api.response.Response;
import com.github.pagehelper.PageInfo;

public interface ISkuApi {

    /**
     * 创建商品（SKU）
     */
    Response<String> createSku(CreateSkuRequestDTO createSkuRequestDTO);

    Response<PageInfo<SkuResponseDTO>> queryList(SkuInventoryPageReq skuInventoryPageReq);

    /**
     * 根据商品ID删除商品及其库存
     */
    Response<Boolean> delete(String goodsId);

    /**
     * 更新商品及库存信息
     */
    Response<Boolean> update(String goodsId, CreateSkuRequestDTO requestDTO);

}
