package com.expensetracker.expense_tracker.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;

@Entity 
@Table(
    name = "budgets",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"month", "year"})
    }
)
public class Budget {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyLimit;
    @Column(nullable = false)
    private int month;
    @Column(nullable = false)
    private int year;
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public Budget() {
    }
    public Budget(Long id, BigDecimal monthlyLimit, int month, int year) {
        this.id = id;
        this.monthlyLimit = monthlyLimit;
        this.month = month;
        this.year = year;
    }
}
