package com.stingray.qello.firetv.inapppurchase.sku;

import com.stingray.qello.firetv.inapppurchase.communication.requestmodel.SvodSubscription;

public class SkuUIData {
    private String productId;
    private SvodSubscription.Recurrence recurrence;
    private String recurrenceTitle;
    private Float price;
    private String currencySymbol;
    private Float originalPrice;
    private Float savingsPercentage;

    private SkuUIData(Builder builder) {
        this.productId = builder.productId;
        this.recurrence = builder.recurrence;
        this.recurrenceTitle = builder.recurrenceTitle;
        this.price = builder.price;
        this.currencySymbol = builder.currencySymbol;
        this.originalPrice = builder.originalPrice;
        this.savingsPercentage = builder.savingsPercentage;
    }

    public String getProductId() {
        return productId;
    }

    public SvodSubscription.Recurrence getRecurrence() {
        return recurrence;
    }

    public String getRecurrenceTitle() {
        return recurrenceTitle;
    }

    public Float getPrice() {
        return price;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public Float getOriginalPrice() {
        return originalPrice;
    }

    public Float getSavingsPercentage() {
        return savingsPercentage;
    }

    public static Builder newBuilder(String productId) {
        return new Builder(productId);
    }

    public static final class Builder {
        private String productId;
        private SvodSubscription.Recurrence recurrence;
        private String recurrenceTitle;
        private Float price;
        private String currencySymbol;
        private Float originalPrice;
        private Float savingsPercentage;

        private Builder(String productId) {
            this.productId = productId;
        }

        public SkuUIData build() {
            return new SkuUIData(this);
        }

        public Builder recurrence(SvodSubscription.Recurrence recurrence) {
            this.recurrence = recurrence;
            return this;
        }

        public Builder recurrenceTitle(String recurrenceTitle) {
            this.recurrenceTitle = recurrenceTitle;
            return this;
        }

        public Builder price(Float price) {
            this.price = price;
            return this;
        }

        public Builder currencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
            return this;
        }

        public Builder originalPrice(Float originalPrice) {
            this.originalPrice = originalPrice;
            return this;
        }

        public Builder savingsPercentage(Float savingsPercentage) {
            this.savingsPercentage = savingsPercentage;
            return this;
        }
    }
}
