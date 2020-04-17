package com.stingray.qello.firetv.inapppurchase.sku;

import android.util.Log;

import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.SvodSubscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SkuUIDataProvider {
    private final String TAG = SkuUIDataProvider.class.getSimpleName();

    private List<SkuUIData> skuUIDatas = new ArrayList<>();

    public SkuUIDataProvider(List<SvodSubscription> svodSubscriptions) {
        List<SvodSubscription.Recurrence> desiredPlans = Arrays.asList(SvodSubscription.Recurrence.MONTHLY, SvodSubscription.Recurrence.YEARLY);

        Map<SvodSubscription.Recurrence, SvodSubscription> skuDataMap = new LinkedHashMap<>();
        for (SvodSubscription.Recurrence recurrence: desiredPlans) {
            for (SvodSubscription svodSubscription : svodSubscriptions) {
                if (svodSubscription.getRecurrence().equalsIgnoreCase(recurrence.name())) {
                    skuDataMap.put(recurrence, svodSubscription);
                    break;
                }
            }
        }

        SvodSubscription monthlySub = skuDataMap.get(SvodSubscription.Recurrence.MONTHLY);
        Float monthlyPrice = evaluatePrice(monthlySub.getPrice(), SvodSubscription.Recurrence.MONTHLY);

        for (Map.Entry<SvodSubscription.Recurrence, SvodSubscription> entry: skuDataMap.entrySet()) {
            SvodSubscription svodSubscription = entry.getValue();
            Float price = evaluatePrice(svodSubscription.getPrice(), entry.getKey());
            String currencySymbol = (svodSubscription.getCurrencySymbol() != null) ? svodSubscription.getCurrencySymbol() : "$";

            SkuUIData.Builder builder = SkuUIData.newBuilder(svodSubscription.getProductId())
                    .price(price)
                    .currencySymbol(currencySymbol)
                    .recurrence(entry.getKey())
                    .recurrenceTitle(svodSubscription.getRecurrenceTitle());

            if (svodSubscription.getRecurrence().equalsIgnoreCase("YEARLY")) {
                Float originalPrice = Math.max(0, monthlyPrice * 12);
                builder.originalPrice(originalPrice);
                builder.savingsPercentage(Math.max(0, (1 - (price / originalPrice))) * 100);
            }

            skuUIDatas.add(builder.build());
        }
    }

    // TODO Leo - This should be coming from Amazon Store - Hardcoded for now
    public List<SkuUIData> get(){
        return skuUIDatas;
    }

    private Float evaluatePrice(String priceString, SvodSubscription.Recurrence recurrence) {
        Float price = null;
        try {
            price = Float.parseFloat(priceString);
        } catch (Exception e) {
            Log.e(TAG, String.format("Failed to parse float [%s]", priceString), e);
        }

        if (price == null) {
            price = (recurrence.equals(SvodSubscription.Recurrence.MONTHLY)) ? 11.99F : 99.99F;
        }

        return price;
    }
}
