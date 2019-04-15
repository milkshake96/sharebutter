package com.fivenine.sharebutter.RecordActivity;

import com.fivenine.sharebutter.models.Item;
import com.fivenine.sharebutter.models.TradeOffer;

public class OfferRecordCard {
    private TradeOffer tradeOffer;
    private Item ownerItem;
    private Item traderItem;

    public TradeOffer getTradeOffer() {
        return tradeOffer;
    }

    public void setTradeOffer(TradeOffer tradeOffer) {
        this.tradeOffer = tradeOffer;
    }

    public Item getOwnerItem() {
        return ownerItem;
    }

    public void setOwnerItem(Item ownerItem) {
        this.ownerItem = ownerItem;
    }

    public Item getTraderItem() {
        return traderItem;
    }

    public void setTraderItem(Item traderItem) {
        this.traderItem = traderItem;
    }
}
