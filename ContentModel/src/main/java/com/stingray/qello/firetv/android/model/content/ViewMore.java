package com.stingray.qello.firetv.android.model.content;

import java.io.Serializable;

public class ViewMore implements Serializable {

    private String itemId;
    private String itemName;

    public ViewMore(String itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
