package cn.bugstack.api.admin;

import cn.bugstack.api.dto.discount.*;
import cn.bugstack.api.response.Response;

import java.util.List;

/**
 * @author needyou
 * @description 折扣管理接口（后台管理）
 * @create 2026-01-13
 */
public interface IDiscountApi {

    Response<String> createDiscount(DiscountCreateRequestDTO requestDTO);

    Response<Boolean> updateDiscount(String discountId, DiscountUpdateRequestDTO requestDTO);

    Response<Boolean> deleteDiscount(String discountId);

    Response<DiscountResponseDTO> queryDiscount(String discountId);

    Response<List<DiscountResponseDTO>> queryDiscountList();
}

