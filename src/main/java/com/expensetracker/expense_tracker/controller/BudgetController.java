package com.expensetracker.expense_tracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expensetracker.expense_tracker.dto.BudgetSummaryResponse;
import com.expensetracker.expense_tracker.entity.Budget;
import com.expensetracker.expense_tracker.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {
    private final BudgetService budgetService;
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

   @GetMapping("/{month}/{year}")
    public ResponseEntity<Budget> getBudget(
        @PathVariable int month,
        @PathVariable int year) {

        Budget budget = budgetService.getBudget(month, year);
        return ResponseEntity.ok(budget);
    }
    
    @PostMapping
    public ResponseEntity<Budget> createOrUpdateBudget(@RequestBody Budget budget) {
        Budget savedBudget = budgetService.createOrUpdateBudget(budget);
        return ResponseEntity.ok(savedBudget);
    }

    @GetMapping("/summary")
    public ResponseEntity<BudgetSummaryResponse> getBudgetSummary(
        @RequestParam int month,
        @RequestParam int year) {

        BudgetSummaryResponse summary =
            budgetService.getBudgetSummary(month, year);

        if (summary == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(summary);
    }
}
