package cn.bugstack.infrastructure.utils.redis.keys;


public final class InventoryKeys {

    private static final String NS = "gbm";
    private static final String SEP = ":";

    private static final String INV = NS + SEP + "inv";
    // 总库存只按 goodsId 维度：gbm:inv:avail:{goodsId}
    private static final String INV_AVAIL = INV + SEP + "avail";
    // 预占记录只按 orderId 维度：gbm:inv:resv:{orderId}
    private static final String INV_RESV  = INV + SEP + "resv";

    private InventoryKeys() {}

    public static String availKey(String goodsId) {
        String gid = requireText(goodsId, "goodsId");
        return INV_AVAIL + SEP + gid;
    }

    public static String resvKey(String orderId) {
        String oid = requireText(orderId, "orderId");
        return INV_RESV + SEP + oid;
    }

    // Helpers

    private static void requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be > 0");
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

}
