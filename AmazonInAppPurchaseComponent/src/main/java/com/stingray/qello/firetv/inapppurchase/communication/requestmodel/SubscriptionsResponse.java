package com.stingray.qello.firetv.inapppurchase.communication.requestmodel;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsResponse {
    private List<SvodSubscription> subscriptionOffers = new ArrayList<>();

    public List<SvodSubscription> getSubscriptionOffers() {
        return subscriptionOffers;
    }
}
