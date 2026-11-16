package com.expensetracker.expense_tracker.dto;

import java.math.BigDecimal;

public class ExpenseSummaryResponse {
    private BigDecimal totalAmount;
    private long totalExpenses;
    private BigDecimal averageExpense;
    private BigDecimal highestExpense;
    private BigDecimal lowestExpense;
    public ExpenseSummaryResponse() {
    }
    public ExpenseSummaryResponse(long totalExpenses,BigDecimal totalAmount, BigDecimal averageExpense, BigDecimal highestExpense, BigDecimal lowestExpense) {
        this.totalAmount = totalAmount;
        this.totalExpenses = totalExpenses;
        this.averageExpense = averageExpense;
        this.highestExpense = highestExpense;
        this.lowestExpense = lowestExpense;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getAverageExpense() {
        return averageExpense;
    }

    public void setAverageExpense(BigDecimal averageExpense) {
        this.averageExpense = averageExpense;
    }

    public BigDecimal getLowestExpense() {
        return lowestExpense;
    }

    public void setLowestExpense(BigDecimal lowestExpense) {
        this.lowestExpense = lowestExpense;
    }

    public BigDecimal getHighestExpense() {
        return highestExpense;
    }

    public void setHighestExpense(BigDecimal highestExpense) {
        this.highestExpense = highestExpense;
    }

    public static class MonthlySpending {

        public MonthlySpending() {
        }
    }
}
