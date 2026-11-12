package com.example.munchly.model;

import java.io.Serializable;

public class Payment implements Serializable {
    private String paymentId;
    private String orderId;
    private double amount;
    private String phoneNumber;
    private String checkoutRequestId;
    private String mpesaReceiptNumber;
    private String paymentStatus; // Pending, Success, Failed, Cancelled
    private long transactionDate;

    public Payment() {}

    public Payment(String paymentId, String orderId, double amount, String phoneNumber, String checkoutRequestId, String mpesaReceiptNumber, String paymentStatus, long transactionDate) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.phoneNumber = phoneNumber;
        this.checkoutRequestId = checkoutRequestId;
        this.mpesaReceiptNumber = mpesaReceiptNumber;
        this.paymentStatus = paymentStatus;
        this.transactionDate = transactionDate;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCheckoutRequestId() { return checkoutRequestId; }
    public void setCheckoutRequestId(String checkoutRequestId) { this.checkoutRequestId = checkoutRequestId; }

    public String getMpesaReceiptNumber() { return mpesaReceiptNumber; }
    public void setMpesaReceiptNumber(String mpesaReceiptNumber) { this.mpesaReceiptNumber = mpesaReceiptNumber; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public long getTransactionDate() { return transactionDate; }
    public void setTransactionDate(long transactionDate) { this.transactionDate = transactionDate; }
}