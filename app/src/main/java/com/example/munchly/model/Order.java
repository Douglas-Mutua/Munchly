package com.example.munchly.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private String customerName;
    private String phone;
    private String deliveryAddress;
    private List<CartItem> items;
    private double totalAmount;
    private String orderStatus; // Pending Payment, Paid, Processing, Shipped, Completed, Cancelled
    private long orderDate;

    public Order() {}

    public Order(String orderId, String userId, String customerName, String phone, String deliveryAddress, List<CartItem> items, double totalAmount, String orderStatus, long orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.customerName = customerName;
        this.phone = phone;
        this.deliveryAddress = deliveryAddress;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public long getOrderDate() { return orderDate; }
    public void setOrderDate(long orderDate) { this.orderDate = orderDate; }
}