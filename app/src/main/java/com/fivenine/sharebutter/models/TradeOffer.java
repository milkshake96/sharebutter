package com.fivenine.sharebutter.models;

public class TradeOffer {

    public static final String TRADE_PENDING = "trade_pending";
    public static final String TRADE_DECLINED = "trade_declined";
    public static final String TRADE_SUCCESSFUL = "trade_successful";

    private long id;
    private String ownerId;
    private String requesterId;
    private String ownerItemId;
    private String requesterItemId;
    private String status;

    public TradeOffer(){}

    public TradeOffer(long id, String ownerId, String requesterId,
                      String ownerItemId, String requesterItemId, String status) {
        this.id = id;
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.ownerItemId = ownerItemId;
        this.requesterItemId = requesterItemId;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public String getOwnerItemId() {
        return ownerItemId;
    }

    public String getRequesterItemId() {
        return requesterItemId;
    }

    public String getStatus() {
        return status;
    }
}
