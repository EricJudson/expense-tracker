package com.expensetracker.expense_tracker.repository;
import com.expensetracker.expense_tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import com.expensetracker.expense_tracker.enums.ExpenseCategory;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import com.expensetracker.expense_tracker.repository.MonthlySpendingProjection;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategory(ExpenseCategory category);
    @Query("""
    SELECT
        YEAR(e.expenseDate) AS year,
        MONTH(e.expenseDate) AS month,
        COUNT(e.id) AS totalExpenses,
        SUM(e.amount) AS totalAmount
    FROM Expense e
    GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate)
    ORDER BY YEAR(e.expenseDate), MONTH(e.expenseDate)
""")
List<MonthlySpendingProjection> getMonthlySpending();
}
