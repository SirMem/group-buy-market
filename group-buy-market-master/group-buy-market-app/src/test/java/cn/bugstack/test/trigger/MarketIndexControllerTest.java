package cn.bugstack.test.trigger;

import cn.bugstack.api.dto.GoodsMarketBatchRequestDTO;
import cn.bugstack.api.dto.GoodsMarketBatchResponseDTO;
import cn.bugstack.api.dto.GoodsMarketRequestDTO;
import cn.bugstack.api.dto.GoodsMarketResponseDTO;
import cn.bugstack.api.dto.MarketGoodsCardDTO;
import cn.bugstack.api.dto.MarketGoodsPageRequestDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.trigger.http.MarketIndexController;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author needyou
 * @description 营销首页服务
 * @create 2025-02-02 16:05
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MarketIndexControllerTest {

    @Resource
    private MarketIndexController marketIndexController;

    @Test
    public void test_queryGroupBuyMarketConfigBatch() {
        GoodsMarketBatchRequestDTO requestDTO = new GoodsMarketBatchRequestDTO();
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setUserId("xfg01");
        requestDTO.setGoodsIdList(Arrays.asList("9890001", "9890002"));
        requestDTO.setIncludeTeamList(false);

        Response<GoodsMarketBatchResponseDTO> response = marketIndexController.queryGroupBuyMarketConfigBatch(requestDTO);

        log.info("请求参数:{}", JSON.toJSONString(requestDTO));
        log.info("应答结果:{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryMarketGoodsCardPage() {
        MarketGoodsPageRequestDTO requestDTO = new MarketGoodsPageRequestDTO();
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setUserId("xfg01");
        requestDTO.setPage(1);
        requestDTO.setPageSize(5);
        requestDTO.setIncludeTeamList(true);
        requestDTO.setIncludeTeamStatistic(true);

        Response<PageInfo<MarketGoodsCardDTO>> response = marketIndexController.queryMarketGoodsPage(requestDTO);

        log.info("请求参数:{}", JSON.toJSONString(requestDTO));
        log.info("应答结果:{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryGroupBuyMarketConfig() {
        GoodsMarketRequestDTO requestDTO = new GoodsMarketRequestDTO();
        requestDTO.setSource("s01");
        requestDTO.setChannel("c01");
        requestDTO.setUserId("xfg01");
        requestDTO.setGoodsId("9890001");

        Response<GoodsMarketResponseDTO> response = marketIndexController.queryGroupBuyMarketConfig(requestDTO);

        log.info("请求参数:{}", JSON.toJSONString(requestDTO));
        log.info("应答结果:{}", JSON.toJSONString(response));
    }

}
