package com.expensetracker.expense_tracker.repository;
import java.math.BigDecimal;
public interface MonthlySpendingProjection {
    int getYear();
    int getMonth();
    long getTotalExpenses();
    BigDecimal getTotalAmount();
}

