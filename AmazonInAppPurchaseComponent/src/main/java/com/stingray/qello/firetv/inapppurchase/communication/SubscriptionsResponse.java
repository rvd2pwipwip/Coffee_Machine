package com.stingray.qello.firetv.inapppurchase.communication;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsResponse {
    private List<SvodSubscription> subscriptionOffers = new ArrayList<>();

    public List<SvodSubscription> getSubscriptionOffers() {
        return subscriptionOffers;
    }
}
