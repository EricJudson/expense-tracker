package com.expensetracker.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.expensetracker.expense_tracker.entity.Budget;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByMonthAndYear(int month, int year);
}
