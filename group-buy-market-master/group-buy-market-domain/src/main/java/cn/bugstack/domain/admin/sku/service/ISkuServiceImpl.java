package cn.bugstack.domain.admin.sku.service;

import cn.bugstack.domain.admin.sku.ISkuService;
import cn.bugstack.domain.admin.sku.adapter.repository.ISkuRepository;
import cn.bugstack.domain.admin.sku.model.entity.SkuInventoryEntity;
import cn.bugstack.domain.admin.sku.model.vo.SkuInfoVO;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
@Slf4j
public class ISkuServiceImpl implements ISkuService {

    @Resource
    private ISkuRepository skuRepository;

    @Override
    public String createSku(String source, String channel, String name, BigDecimal originalPrice) {

        String goodsId = skuRepository.insertSku(source, channel, name, originalPrice);
        return goodsId;
    }

    @Override
    public void createInventory(String goodsId, Long totalStock) {
        skuRepository.insertInventoryWithoutActivityId(goodsId, totalStock);
    }

    @Override
    public String createSkuAndInventory(String source, String channel, String name, BigDecimal originalPrice, Long totalStock) {

        String goodsId = skuRepository.createSkuAndInventory(
                source,
                channel,
                name,
                originalPrice,
                totalStock
        );
        return goodsId;
    }

    @Override
    public PageInfo<SkuInfoVO> querySkuPage(SkuInventoryEntity skuInventoryEntity) {
        return skuRepository.querySkuInfoPage(skuInventoryEntity);
    }

    @Override
    public void deleteSku(String goodsId) {
        skuRepository.deleteSkuAndInventory(goodsId);
    }

    @Override
    public void updateSkuAndInventory(String goodsId, String name, BigDecimal originalPrice, Long totalStock) {
        skuRepository.updateSkuAndInventory(goodsId, name, originalPrice, totalStock);
    }
}
