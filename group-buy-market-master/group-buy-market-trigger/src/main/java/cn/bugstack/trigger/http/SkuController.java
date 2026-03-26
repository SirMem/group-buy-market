package cn.bugstack.trigger.http;

import cn.bugstack.api.admin.ISkuApi;
import cn.bugstack.api.dto.CreateSkuRequestDTO;
import cn.bugstack.api.dto.SkuInventoryPageReq;
import cn.bugstack.api.dto.SkuResponseDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.admin.sku.ISkuService;
import cn.bugstack.domain.admin.sku.model.entity.SkuInventoryEntity;
import cn.bugstack.domain.admin.sku.model.vo.SkuInfoVO;
import cn.bugstack.types.enums.ResponseCode;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author needyou
 * @description 商家商品管理接口
 * @create 2025-12-02 10:10
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/merchant/sku/")
public class SkuController implements ISkuApi {


    @Resource
    private ISkuService merchantSkuService;

    /**
     * 商家创建商品
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public Response<String> createSku(@RequestBody CreateSkuRequestDTO requestDTO) {
        try {
            log.info("商家创建商品开始 source:{} channel:{} name:{}",
                    requestDTO.getSource(), requestDTO.getChannel(), requestDTO.getName());

            // 基础参数校验
            if (StringUtils.isBlank(requestDTO.getSource())
                    || StringUtils.isBlank(requestDTO.getChannel())
                    || StringUtils.isBlank(requestDTO.getName())
                    || null == requestDTO.getPrice()) {
                return Response.<String>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 分转元
            BigDecimal originalPrice = BigDecimal
                    .valueOf(requestDTO.getPrice())
                    .movePointLeft(2);

            String goodsId = merchantSkuService.createSkuAndInventory(
                    requestDTO.getSource(),
                    requestDTO.getChannel(),
                    requestDTO.getName(),
                    originalPrice,
                    requestDTO.getTotalStock()
            );

            return Response.<String>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(goodsId)
                    .build();
        } catch (Exception e) {
            log.error("商家创建商品失败 source:{} channel:{} name:{}",
                    requestDTO.getSource(), requestDTO.getChannel(), requestDTO.getName(), e);
            return Response.<String>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query",  method = RequestMethod.GET)
    public Response<PageInfo<SkuResponseDTO>> queryList(SkuInventoryPageReq skuInventoryPageReq) {
        try {
            SkuInventoryEntity skuInventoryEntity = new SkuInventoryEntity();
            BeanUtils.copyProperties(skuInventoryPageReq, skuInventoryEntity);

            PageInfo<SkuInfoVO> skuInfoVOPage = merchantSkuService.querySkuPage(skuInventoryEntity);

            List<SkuResponseDTO> dtoList = skuInfoVOPage.getList().stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            PageInfo<SkuResponseDTO> dtoPage = new PageInfo<>();
            BeanUtils.copyProperties(skuInfoVOPage, dtoPage, "list");
            dtoPage.setList(dtoList);

            return Response.<PageInfo<SkuResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoPage)
                    .build();
        } catch (Exception e) {
            log.error("商家查询商品列表失败 req:{}", skuInventoryPageReq, e);
            return Response.<PageInfo<SkuResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                .build();
        }
    }

    /**
     * 查询商品 goodsId 列表（分页）- 供前端 Index 页批量拉取 goodsId 使用
     */
    @RequestMapping(value = "query_goods_id_page", method = RequestMethod.GET)
    public Response<PageInfo<String>> queryGoodsIdPage(SkuInventoryPageReq skuInventoryPageReq) {
        try {
            SkuInventoryEntity skuInventoryEntity = new SkuInventoryEntity();
            BeanUtils.copyProperties(skuInventoryPageReq, skuInventoryEntity);

            PageInfo<SkuInfoVO> skuInfoVOPage = merchantSkuService.querySkuPage(skuInventoryEntity);

            List<String> goodsIdList = skuInfoVOPage.getList().stream()
                    .map(SkuInfoVO::getGoods)
                    .filter(Objects::nonNull)
                    .map(SkuInfoVO.Goods::getGoodsId)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toList());

            PageInfo<String> dtoPage = new PageInfo<>();
            BeanUtils.copyProperties(skuInfoVOPage, dtoPage, "list");
            dtoPage.setList(goodsIdList);

            return Response.<PageInfo<String>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(dtoPage)
                    .build();
        } catch (Exception e) {
            log.error("商家查询goodsId列表失败 req:{}", skuInventoryPageReq, e);
            return Response.<PageInfo<String>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 根据商品ID删除商品及其库存
     */
    @Override
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public Response<Boolean> delete(@RequestParam("goodsId") String goodsId) {
        try {
            if (StringUtils.isBlank(goodsId)) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            merchantSkuService.deleteSku(goodsId);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(Boolean.TRUE)
                    .build();
        } catch (Exception e) {
            log.error("商家删除商品失败 goodsId:{}", goodsId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }

    /**
     * 更新商品与库存信息（goodsId 从列表/创建接口获得）
     */
    @Override
    @RequestMapping(value = "update", method = RequestMethod.PUT)
    public Response<Boolean> update(@RequestParam("goodsId") String goodsId,
                                    @RequestBody CreateSkuRequestDTO requestDTO) {
        try {
            if (StringUtils.isBlank(goodsId)) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }
            if (requestDTO == null) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }
            if (StringUtils.isBlank(requestDTO.getName())
                    && requestDTO.getPrice() == null
                    && requestDTO.getTotalStock() == null) {
                return Response.<Boolean>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .data(Boolean.FALSE)
                        .build();
            }

            // 分转元（允许 price 为空：只更新名称或库存时不转换）
            BigDecimal originalPrice = null;
            if (requestDTO.getPrice() != null) {
                originalPrice = BigDecimal.valueOf(requestDTO.getPrice()).movePointLeft(2);
            }

            merchantSkuService.updateSkuAndInventory(
                    goodsId,
                    requestDTO.getName(),
                    originalPrice,
                    requestDTO.getTotalStock()
            );

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(Boolean.TRUE)
                    .build();
        } catch (Exception e) {
            log.error("商家更新商品失败 goodsId:{} req:{}", goodsId, requestDTO, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .data(Boolean.FALSE)
                    .build();
        }
    }

    /**
     * 领域层 VO -> API 层 DTO 映射
     */
    private SkuResponseDTO convertToResponseDTO(SkuInfoVO skuInfoVO) {
        if (skuInfoVO == null) {
            return null;
        }

        SkuInfoVO.Goods goods = skuInfoVO.getGoods();
        SkuInfoVO.Inventory inventory = skuInfoVO.getInventory();
        List<SkuInfoVO.ActivityBinding> activityBindings = skuInfoVO.getActivityBindings();

        SkuResponseDTO.Goods goodsDTO = null;
        if (goods != null) {
            goodsDTO = SkuResponseDTO.Goods.builder()
                    .goodsId(goods.getGoodsId())
                    .source(goods.getSource())
                    .channel(goods.getChannel())
                    .name(goods.getName())
                    .categoryId(goods.getCategoryId())
                    .subtitle(goods.getSubtitle())
                    .description(goods.getDescription())
                    .coverImage(goods.getCoverImage())
                    .imageList(goods.getImageList())
                    .price(goods.getPrice())
                    .status(goods.getStatus())
                    .createTime(goods.getCreateTime())
                    .updateTime(goods.getUpdateTime())
                    .build();
        }

        SkuResponseDTO.Inventory inventoryDTO = null;
        if (inventory != null) {
            inventoryDTO = SkuResponseDTO.Inventory.builder()
                    .totalStock(inventory.getTotalStock())
                    .reservedStock(inventory.getReservedStock())
                    .soldStock(inventory.getSoldStock())
                    .availableStock(inventory.getAvailableStock())
                    .build();
        }

        List<SkuResponseDTO.ActivityBinding> activityBindingDTOList = null;
        if (activityBindings != null && !activityBindings.isEmpty()) {
            activityBindingDTOList = activityBindings.stream()
                    .map(binding -> SkuResponseDTO.ActivityBinding.builder()
                            .activityId(binding.getActivityId())
                            .activityName(binding.getActivityName())
                            .status(binding.getStatus())
                            .groupType(binding.getGroupType())
                            .startTime(binding.getStartTime())
                            .endTime(binding.getEndTime())
                            .build())
                    .collect(Collectors.toList());
        }

        return SkuResponseDTO.builder()
                .goods(goodsDTO)
                .inventory(inventoryDTO)
                .activityBindings(activityBindingDTOList)
                .build();
    }


}
