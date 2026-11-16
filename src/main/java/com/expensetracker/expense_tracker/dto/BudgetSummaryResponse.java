package com.expensetracker.expense_tracker.dto;

import java.math.BigDecimal;

public class BudgetSummaryResponse {
    private BigDecimal monthlyLimit;
    private BigDecimal totalSpent;
    private BigDecimal remainingBudget;
    private BigDecimal BudgetUtilization;
    private boolean exceeded;
    public BudgetSummaryResponse() {
    }
    public BudgetSummaryResponse(BigDecimal monthlyLimit, BigDecimal totalSpent, BigDecimal remainingBudget, BigDecimal BudgetUtilization, boolean exceeded) {
        this.monthlyLimit = monthlyLimit;
        this.totalSpent = totalSpent;
        this.remainingBudget = remainingBudget;
        this.BudgetUtilization = BudgetUtilization;
        this.exceeded = exceeded;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(BigDecimal remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public BigDecimal getBudgetUtilization() {
        return BudgetUtilization;
    }

    public void setBudgetUtilization(BigDecimal BudgetUtilization) {
        this.BudgetUtilization = BudgetUtilization;
    }

    public boolean isExceeded() {
        return exceeded;
    }

    public void setExceeded(boolean exceeded) {
        this.exceeded = exceeded;
    }
    
}
