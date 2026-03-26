package cn.bugstack.domain.admin.discount;

import cn.bugstack.api.dto.discount.DiscountCreateRequestDTO;
import cn.bugstack.api.dto.discount.DiscountResponseDTO;
import cn.bugstack.api.dto.discount.DiscountUpdateRequestDTO;
import cn.bugstack.api.response.Response;

import java.util.List;

public interface IDiscountService {

    Response<String> createDiscount(DiscountCreateRequestDTO requestDTO);

    Response<Boolean> updateDiscount(String discountId, DiscountUpdateRequestDTO requestDTO);

    Response<Boolean> deleteDiscount(String discountId);

    Response<DiscountResponseDTO> queryDiscount(String discountId);

    Response<List<DiscountResponseDTO>> queryDiscountList();

}
