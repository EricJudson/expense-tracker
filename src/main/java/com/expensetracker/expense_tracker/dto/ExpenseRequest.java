package com.expensetracker.expense_tracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.expensetracker.expense_tracker.enums.ExpenseCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


public class ExpenseRequest {
    @NotBlank
    private String title;
    @NotNull @Positive
    private BigDecimal amount;
    private String description;
    @NotNull
    private LocalDate expenseDate;
    @NotNull 
    private ExpenseCategory category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public ExpenseRequest() {
}

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

}
