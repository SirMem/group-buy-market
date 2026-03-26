package cn.bugstack.test.trigger;

import cn.bugstack.api.admin.ISkuApi;
import cn.bugstack.api.dto.CreateSkuRequestDTO;
import cn.bugstack.api.dto.SkuInventoryPageReq;
import cn.bugstack.api.dto.SkuResponseDTO;
import cn.bugstack.api.response.Response;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author needyou
 * @description 商品(SKU)管理接口集成测试
 * @create 2026-03-21
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SkuControllerTest {

    @Resource
    private ISkuApi skuController;

    @Test
    public void test_createSku() {
        CreateSkuRequestDTO requestDTO = new CreateSkuRequestDTO();
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setName("测试拼团商品");
        requestDTO.setSubtitle("高品质拼团商品");
        requestDTO.setDescription("商品详情描述");
        requestDTO.setPrice(19900L);
        requestDTO.setTotalStock(100L);
        requestDTO.setStatus("ONLINE");

        Response<String> response = skuController.createSku(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_querySku() {
        SkuInventoryPageReq requestDTO = new SkuInventoryPageReq();
        requestDTO.setPage(1);
        requestDTO.setPageSize(10);

        Response<PageInfo<SkuResponseDTO>> response = skuController.queryList(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_queryGoodsIdPage() {
        SkuInventoryPageReq requestDTO = new SkuInventoryPageReq();
        requestDTO.setPage(1);
        requestDTO.setPageSize(5);
        requestDTO.setKeyword("测试");

        Response<PageInfo<SkuResponseDTO>> response = skuController.queryList(requestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_updateSku() {
        // 先查询列表，取第一个goodsId
        SkuInventoryPageReq pageReq = new SkuInventoryPageReq();
        pageReq.setPage(1);
        pageReq.setPageSize(10);
        Response<PageInfo<SkuResponseDTO>> listResponse = skuController.queryList(pageReq);
        if (listResponse.getData() == null || listResponse.getData().getList() == null
                || listResponse.getData().getList().isEmpty()) {
            log.info("暂无商品数据，请先执行 test_createSku");
            return;
        }
        String goodsId = listResponse.getData().getList().get(0).getGoods().getGoodsId();

        CreateSkuRequestDTO requestDTO = new CreateSkuRequestDTO();
        requestDTO.setName("测试拼团商品（已更新）");
        requestDTO.setPrice(18900L);
        requestDTO.setTotalStock(200L);
        requestDTO.setStatus("ONLINE");

        Response<Boolean> response = skuController.update(goodsId, requestDTO);

        log.info("测试结果 goodsId:{} req:{} res:{}", goodsId, JSON.toJSONString(requestDTO), JSON.toJSONString(response));
    }

    @Test
    public void test_deleteSku() {
        SkuInventoryPageReq pageReq = new SkuInventoryPageReq();
        pageReq.setPage(1);
        pageReq.setPageSize(10);
        Response<PageInfo<SkuResponseDTO>> listResponse = skuController.queryList(pageReq);
        if (listResponse.getData() == null || listResponse.getData().getList() == null
                || listResponse.getData().getList().isEmpty()) {
            log.info("暂无商品数据");
            return;
        }
        java.util.List<SkuResponseDTO> list = listResponse.getData().getList();
        String goodsId = list.get(list.size() - 1).getGoods().getGoodsId();

        Response<Boolean> response = skuController.delete(goodsId);

        log.info("测试结果 goodsId:{} res:{}", goodsId, JSON.toJSONString(response));
    }

}
