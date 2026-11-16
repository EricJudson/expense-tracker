package com.expensetracker.expense_tracker.dto;

import java.math.BigDecimal;
public class MonthlySpendingResponse {
    private int year;
    private int month;
    private long totalExpenses;
    private BigDecimal totalAmount;

    public MonthlySpendingResponse() {
    }
    public MonthlySpendingResponse(int year, int month, long totalExpenses, BigDecimal totalAmount) {
        this.year = year;
        this.month = month;
        this.totalExpenses = totalExpenses;
        this.totalAmount = totalAmount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
