package com.raremarket.backend.dto.admin;

import java.util.ArrayList;
import java.util.List;

public class AdminMetricsResponse {
    private Summary summary;
    private Series ordersTrend;
    private Series revenueTrend;
    private Series responseTimeTrend;
    private Breakdown orderStatuses;
    private Breakdown topCategories;
    private Breakdown userRoles;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public Series getOrdersTrend() {
        return ordersTrend;
    }

    public void setOrdersTrend(Series ordersTrend) {
        this.ordersTrend = ordersTrend;
    }

    public Series getRevenueTrend() {
        return revenueTrend;
    }

    public void setRevenueTrend(Series revenueTrend) {
        this.revenueTrend = revenueTrend;
    }

    public Series getResponseTimeTrend() {
        return responseTimeTrend;
    }

    public void setResponseTimeTrend(Series responseTimeTrend) {
        this.responseTimeTrend = responseTimeTrend;
    }

    public Breakdown getOrderStatuses() {
        return orderStatuses;
    }

    public void setOrderStatuses(Breakdown orderStatuses) {
        this.orderStatuses = orderStatuses;
    }

    public Breakdown getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(Breakdown topCategories) {
        this.topCategories = topCategories;
    }

    public Breakdown getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Breakdown userRoles) {
        this.userRoles = userRoles;
    }

    public static class Summary {
        private long totalUsers;
        private long adminUsers;
        private long totalItems;
        private long availableItems;
        private long totalOrders;
        private double totalRevenue;
        private long ordersLast30Days;
        private double revenueLast30Days;
        private long activeBuyersLast30Days;
        private long activeSellersLast30Days;

        public long getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(long totalUsers) {
            this.totalUsers = totalUsers;
        }

        public long getAdminUsers() {
            return adminUsers;
        }

        public void setAdminUsers(long adminUsers) {
            this.adminUsers = adminUsers;
        }

        public long getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(long totalItems) {
            this.totalItems = totalItems;
        }

        public long getAvailableItems() {
            return availableItems;
        }

        public void setAvailableItems(long availableItems) {
            this.availableItems = availableItems;
        }

        public long getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(long totalOrders) {
            this.totalOrders = totalOrders;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public long getOrdersLast30Days() {
            return ordersLast30Days;
        }

        public void setOrdersLast30Days(long ordersLast30Days) {
            this.ordersLast30Days = ordersLast30Days;
        }

        public double getRevenueLast30Days() {
            return revenueLast30Days;
        }

        public void setRevenueLast30Days(double revenueLast30Days) {
            this.revenueLast30Days = revenueLast30Days;
        }

        public long getActiveBuyersLast30Days() {
            return activeBuyersLast30Days;
        }

        public void setActiveBuyersLast30Days(long activeBuyersLast30Days) {
            this.activeBuyersLast30Days = activeBuyersLast30Days;
        }

        public long getActiveSellersLast30Days() {
            return activeSellersLast30Days;
        }

        public void setActiveSellersLast30Days(long activeSellersLast30Days) {
            this.activeSellersLast30Days = activeSellersLast30Days;
        }
    }

    public static class Series {
        private List<String> labels = new ArrayList<>();
        private List<Double> values = new ArrayList<>();

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Double> getValues() {
            return values;
        }

        public void setValues(List<Double> values) {
            this.values = values;
        }
    }

    public static class Breakdown {
        private List<String> labels = new ArrayList<>();
        private List<Long> values = new ArrayList<>();

        public List<String> getLabels() {
            return labels;
        }

        public void setLabels(List<String> labels) {
            this.labels = labels;
        }

        public List<Long> getValues() {
            return values;
        }

        public void setValues(List<Long> values) {
            this.values = values;
        }
    }
}