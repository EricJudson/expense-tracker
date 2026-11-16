package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.dto.BudgetSummaryResponse;
import com.expensetracker.expense_tracker.entity.Budget;
import com.expensetracker.expense_tracker.repository.BudgetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private ExpenseRepository expenseRepository;

    public Budget createOrUpdateBudget(Budget budget) {

        Budget existingBudget = budgetRepository
                .findByMonthAndYear(budget.getMonth(), budget.getYear())
                .orElse(null);
    
        if (existingBudget != null) {
            existingBudget.setMonthlyLimit(budget.getMonthlyLimit());
            return budgetRepository.save(existingBudget);
        }

        return budgetRepository.save(budget);
    }

    public Budget getBudget(int month, int year) {
        return budgetRepository.findByMonthAndYear(month, year).orElse(null);
    }

    public BudgetSummaryResponse getBudgetSummary(int month, int year) {
    
        Budget budget = getBudget(month, year);
    
        if (budget == null) {
            return null;
        }
    
        BigDecimal monthlyLimit = budget.getMonthlyLimit();
    
        // Get expenses for the requested month and year
        List<Expense> expenses = expenseRepository.findAll().stream()
                .filter(expense ->
                        expense.getExpenseDate().getMonthValue() == month &&
                        expense.getExpenseDate().getYear() == year
                )
                .toList();
    
        // Calculate total spending
        BigDecimal totalSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    
        // Calculate remaining budget
        BigDecimal remainingBudget = monthlyLimit.subtract(totalSpent);
    
        // Calculate budget utilization
        BigDecimal budgetUtilization = BigDecimal.ZERO;
    
        if (monthlyLimit.compareTo(BigDecimal.ZERO) > 0) {
            budgetUtilization = totalSpent
                    .multiply(BigDecimal.valueOf(100))
                    .divide(monthlyLimit, 2, RoundingMode.HALF_UP);
        }
    
        // Check whether budget is exceeded
        boolean exceeded = totalSpent.compareTo(monthlyLimit) > 0;
    
        return new BudgetSummaryResponse(
                monthlyLimit,
                totalSpent,
                remainingBudget,
                budgetUtilization,
                exceeded
        );
    }
}
