package cn.bugstack.domain.trade.service;

public interface ITradeInventoryService {

    /**
     * 预占总库存（Try）
     *
     * 总库存只按 goodsId 维度做 TCC，不再携带 activityId 作为库存键的一部分。
     */
    String reserveTotalStockTry(String goodsId, String orderId, int ttlMinutes);

    void confirm(String orderId);

    void cancel(String orderId, String goodsId);
}
