package com.wadidejla.settings;

/**
 * Created by snouto on 23/07/15.
 */
public class TabDetailsCounters {

    // NEW_REQUESTS , OUTGOING,CHECKIN,SORTING,RECEIVED,DISTRIBUTE,COLLECT,TRANSFER

    private int newRequestsCount;
    private int outgoingCount;
    private int CheckInCount;
    private int sortingCount;
    private int receivedCount;
    private int distributeCount;
    private int collectCount;
    private int transferCount;


    public int getNewRequestsCount() {
        return newRequestsCount;
    }

    public void setNewRequestsCount(int newRequestsCount) {
        this.newRequestsCount = newRequestsCount;
    }

    public int getOutgoingCount() {
        return outgoingCount;
    }

    public void setOutgoingCount(int outgoingCount) {
        this.outgoingCount = outgoingCount;
    }

    public int getCheckInCount() {
        return CheckInCount;
    }

    public void setCheckInCount(int checkInCount) {
        CheckInCount = checkInCount;
    }

    public int getSortingCount() {
        return sortingCount;
    }

    public void setSortingCount(int sortingCount) {
        this.sortingCount = sortingCount;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
    }

    public int getDistributeCount() {
        return distributeCount;
    }

    public void setDistributeCount(int distributeCount) {
        this.distributeCount = distributeCount;
    }

    public int getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(int collectCount) {
        this.collectCount = collectCount;
    }

    public int getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(int transferCount) {
        this.transferCount = transferCount;
    }
}
