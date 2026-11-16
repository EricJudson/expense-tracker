package com.expensetracker.expense_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.enums.ExpenseCategory;
// import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.service.ExpenseService;
import com.expensetracker.expense_tracker.dto.ExpenseSummaryResponse;
import com.expensetracker.expense_tracker.dto.MonthlySpendingResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    private ExpenseService expenseService;
    // private ExpenseRepository expenseRepository;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public List<ExpenseResponse> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {

    return ResponseEntity.ok(expenseService.getExpenseById(id));
}

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
        @RequestBody @Valid ExpenseRequest expense) {

        ExpenseResponse savedExpense = expenseService.createExpense(expense);

        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, @RequestBody @Valid ExpenseRequest expenseRequest) {
        ExpenseResponse updatedExpense = expenseService.updateExpense(id, expenseRequest);
        if (updatedExpense != null) {
            return ResponseEntity.ok(updatedExpense);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        boolean exists = expenseService.deleteExpense(id);
        if (exists) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByCategory(@PathVariable ExpenseCategory category) {
        List<ExpenseResponse> expenses = expenseService.getExpensesByCategory(category);
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/summary")
    public ResponseEntity<ExpenseSummaryResponse> getExpenseSummary() {
        ExpenseSummaryResponse summary = expenseService.getExpenseSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/monthly-spending")
    public ResponseEntity<List<MonthlySpendingResponse>> getMonthlySpending() {
        List<MonthlySpendingResponse> monthlySpending = expenseService.getMonthlySpending();
        return ResponseEntity.ok(monthlySpending);
    }
}
