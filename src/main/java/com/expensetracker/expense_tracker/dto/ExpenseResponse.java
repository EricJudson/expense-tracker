package com.expensetracker.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.expensetracker.expense_tracker.enums.ExpenseCategory;

public class ExpenseResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private LocalDateTime createdAt;
    private ExpenseCategory category;
    public ExpenseResponse() {
    }

    public ExpenseResponse(Long id, String title, String description, BigDecimal amount, LocalDate expenseDate, LocalDateTime createdAt, ExpenseCategory category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.createdAt = createdAt;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }
    
    
}
