package com.expensetracker.expense_tracker.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.dto.ExpenseSummaryResponse;
import com.expensetracker.expense_tracker.dto.MonthlySpendingResponse;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.exception.ExpenseNotFoundException;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.enums.ExpenseCategory;

import java.math.RoundingMode;


@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }
    
    public ExpenseResponse createExpense(ExpenseRequest expense) {
        Expense newExpense = new Expense();
        newExpense.setTitle(expense.getTitle());
        newExpense.setDescription(expense.getDescription());
        newExpense.setAmount(expense.getAmount());
        newExpense.setExpenseDate(expense.getExpenseDate());
        newExpense.setCategory(expense.getCategory());
        Expense savedExpense = expenseRepository.save(newExpense);
        return convertToExpenseResponse(savedExpense);
    }

    public List<ExpenseResponse> getAllExpenses() {
        List<Expense> expenses = expenseRepository.findAll();
        return expenses.stream()
                .map(this::convertToExpenseResponse)
                .toList();
    }

    public ExpenseResponse getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .map(this::convertToExpenseResponse)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
    }

    /*public Expense updateExpense(Long id, Expense expense) {
        Expense existingExpense = expenseRepository.findById(id).orElse(null);
        if (existingExpense != null) {
            existingExpense.setTitle(expense.getTitle());
            existingExpense.setAmount(expense.getAmount());
            existingExpense.setDescription(expense.getDescription());
            existingExpense.setExpenseDate(expense.getExpenseDate());
            expenseRepository.save(existingExpense);
        }
        return existingExpense;
    }*/
   public ExpenseResponse updateExpense(Long id, ExpenseRequest expenseRequest) {
        Expense existingExpense = expenseRepository.findById(id).orElseThrow(() -> new ExpenseNotFoundException("Expense not found with id: " + id));
            existingExpense.setTitle(expenseRequest.getTitle());
            existingExpense.setDescription(expenseRequest.getDescription());
            existingExpense.setAmount(expenseRequest.getAmount());
            existingExpense.setExpenseDate(expenseRequest.getExpenseDate());
            existingExpense.setCategory(expenseRequest.getCategory());
            Expense updatedExpense = expenseRepository.save(existingExpense);
            return convertToExpenseResponse(updatedExpense);
    }

    public boolean deleteExpense(Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<ExpenseResponse> getExpensesByCategory(ExpenseCategory category) {
        List<Expense> expenses = expenseRepository.findByCategory(category);
        List<ExpenseResponse> expenseResponses = expenses.stream()
                .map(this::convertToExpenseResponse)
                .toList();
        return expenseResponses;
    }

    public ExpenseResponse convertToExpenseResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getCategory()
        );
    }

    public ExpenseSummaryResponse getExpenseSummary() {
        List<Expense> expenses = expenseRepository.findAll();
        BigDecimal totalAmount = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalExpenses = expenses.size();
        BigDecimal averageExpense = totalExpenses > 0
        ? totalAmount.divide(
                BigDecimal.valueOf(totalExpenses),
                2,
                RoundingMode.HALF_UP
          )
        : BigDecimal.ZERO;
        BigDecimal highestExpense = expenses.stream()
                .map(Expense::getAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal lowestExpense = expenses.stream()
                .map(Expense::getAmount)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        return new ExpenseSummaryResponse(
            totalExpenses,
            totalAmount,
            averageExpense,
            highestExpense,
            lowestExpense
        );
    }

    public List<MonthlySpendingResponse> getMonthlySpending(){
        
        List<MonthlySpendingResponse> monthlySpending = expenseRepository.getMonthlySpending().stream()
                .map(projection -> new MonthlySpendingResponse(
                        projection.getYear(),
                        projection.getMonth(),
                        projection.getTotalExpenses(),
                        projection.getTotalAmount()
                ))
                .toList();
        return monthlySpending;
    }
}
