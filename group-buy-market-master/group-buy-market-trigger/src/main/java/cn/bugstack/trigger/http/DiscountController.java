package cn.bugstack.trigger.http;

import cn.bugstack.api.admin.IDiscountApi;
import cn.bugstack.api.dto.discount.DiscountCreateRequestDTO;
import cn.bugstack.api.dto.discount.DiscountResponseDTO;
import cn.bugstack.api.dto.discount.DiscountUpdateRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.admin.discount.IDiscountService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author needyou
 * @description 折扣管理接口（后台管理）
 * @create 2026-01-13
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/merchant/discount/")
public class DiscountController implements IDiscountApi {

    @Resource
    private IDiscountService discountService;

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Response<String> createDiscount(@RequestBody DiscountCreateRequestDTO requestDTO) {
        return discountService.createDiscount(requestDTO);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public Response<Boolean> updateDiscount(@RequestParam("discountId") String discountId,
                                            @RequestBody DiscountUpdateRequestDTO requestDTO) {
        return discountService.updateDiscount(discountId, requestDTO);
    }

    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public Response<Boolean> deleteDiscount(@RequestParam("discountId") String discountId) {
        return discountService.deleteDiscount(discountId);
    }

    @RequestMapping(value = "query", method = RequestMethod.GET)
    public Response<DiscountResponseDTO> queryDiscount(@RequestParam("discountId") String discountId) {
        return discountService.queryDiscount(discountId);
    }

    @RequestMapping(value = "query_list", method = RequestMethod.GET)
    public Response<List<DiscountResponseDTO>> queryDiscountList() {
        return discountService.queryDiscountList();
    }

}
