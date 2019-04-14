package com.fivenine.sharebutter.models;

public class TradeOffer {

    public static final String TRADE_PENDING = "trade_pending";
    public static final String TRADE_DECLINED = "trade_declined";
    public static final String TRADE_SUCCESSFUL = "trade_successful";

    private long id;
    private String ownerId;
    private String ownerUsername;
    private String ownerEmail;
    private String ownerPhoneNumber;
    private String requesterId;
    private String requesterUsername;
    private String requesterEmail;
    private String requesterPhoneNumber;
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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setRequesterItemId(String requesterItemId) {
        this.requesterItemId = requesterItemId;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterPhoneNumber() {
        return requesterPhoneNumber;
    }

    public void setRequesterPhoneNumber(String requesterPhoneNumber) {
        this.requesterPhoneNumber = requesterPhoneNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
