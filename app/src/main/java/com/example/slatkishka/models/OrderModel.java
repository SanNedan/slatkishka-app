package com.example.slatkishka.models;

import java.util.Map;

// модел за нарачки

public class OrderModel {
    public String orderId;
    public String customerUsername;
    public String businessUsername;

    public Map<String, Integer> items; // производите и количините
    public String status; // на нарачката

    public OrderModel() {} // празен конструктор за Firebase

    // конструкторот, како и get и set методите ги генерирав со Generate опцијата
    public OrderModel(String status, Map<String, Integer> items, String businessUsername, String customerUsername, String orderId) {
        this.status = status;
        this.items = items;
        this.businessUsername = businessUsername;
        this.customerUsername = customerUsername;
        this.orderId = orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public void setBusinessUsername(String businessUsername) {
        this.businessUsername = businessUsername;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getBusinessUsername() {
        return businessUsername;
    }

    public String getStatus() {
        return status;
    }

    public Map<String, Integer> getItems() {
        return items;
    }
}
