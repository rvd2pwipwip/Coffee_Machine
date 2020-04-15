package com.stingray.qello.firetv.inapppurchase.sku;

import java.util.HashMap;
import java.util.Map;

public class SkuUIDataProvider {
    private final Map<String, SkuUIData> skuDataMap;

    public SkuUIDataProvider() {
        skuDataMap = new HashMap<>();
        skuDataMap.put("com.qello.subscription.1mo.1199.trial14", new SkuUIData("11.99$", "per month after trial"));
        skuDataMap.put("qc.yearly.amazon.1", new SkuUIData("99.99$","143.99$", "No free trial", "Save", "30%"));
    }

    // TODO Leo - This should be coming from Amazon Store - Hardcoded for now
    public SkuUIData getSkuUIData(String sku) throws SkuDataNotFoundException {
        SkuUIData skuUIData = skuDataMap.get(sku);
        if (skuUIData != null) {
            return skuUIData;
        } else {
            throw new SkuDataNotFoundException(sku);
        }
    }

    public static class SkuDataNotFoundException extends Exception {
        SkuDataNotFoundException(String sku) {
            super(String.format("Unable to find sku data for sku [%s]", sku));
        }
    }
}
