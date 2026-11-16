
package com.expensetracker.expense_tracker.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expensetracker.expense_tracker.dto.BudgetSummaryResponse;
import com.expensetracker.expense_tracker.entity.Budget;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.repository.BudgetRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private BudgetService budgetService;


    // Test 1: Create a new budget

    @Test
    void createOrUpdateBudget_ShouldCreateNewBudget() {

        Budget budget = new Budget(
                null,
                new BigDecimal("20000.00"),
                11,
                2026
        );

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Budget result = budgetService.createOrUpdateBudget(budget);

        assertEquals(new BigDecimal("20000.00"), result.getMonthlyLimit());
        assertEquals(11, result.getMonth());
        assertEquals(2026, result.getYear());

        verify(budgetRepository, times(1))
                .findByMonthAndYear(11, 2026);

        verify(budgetRepository, times(1))
                .save(any(Budget.class));
    }


    // Test 2: Update an existing budget

    @Test
    void createOrUpdateBudget_ShouldUpdateExistingBudget() {

        Budget existingBudget = new Budget(
                1L,
                new BigDecimal("15000.00"),
                11,
                2026
        );

        Budget updatedBudget = new Budget(
                null,
                new BigDecimal("25000.00"),
                11,
                2026
        );

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.of(existingBudget));

        when(budgetRepository.save(any(Budget.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Budget result = budgetService.createOrUpdateBudget(updatedBudget);

        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("25000.00"), result.getMonthlyLimit());

        verify(budgetRepository, times(1))
                .findByMonthAndYear(11, 2026);

        verify(budgetRepository, times(1))
                .save(existingBudget);
    }


    // Test 3: Retrieve an existing budget

    @Test
    void getBudget_ShouldReturnBudget_WhenBudgetExists() {

        Budget budget = new Budget(
                1L,
                new BigDecimal("20000.00"),
                11,
                2026
        );

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.of(budget));

        Budget result = budgetService.getBudget(11, 2026);

        assertNotNull(result);
        assertEquals(new BigDecimal("20000.00"), result.getMonthlyLimit());

        verify(budgetRepository, times(1))
                .findByMonthAndYear(11, 2026);
    }


    // Test 4: Return null when budget does not exist

    @Test
    void getBudget_ShouldReturnNull_WhenBudgetDoesNotExist() {

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.empty());

        Budget result = budgetService.getBudget(11, 2026);

        assertNull(result);

        verify(budgetRepository, times(1))
                .findByMonthAndYear(11, 2026);
    }


    // Test 5: Calculate budget summary correctly

    @Test
    void getBudgetSummary_ShouldCalculateCorrectly() {

        Budget budget = new Budget(
                1L,
                new BigDecimal("20000.00"),
                11,
                2026
        );

        Expense expense1 = new Expense();
        expense1.setAmount(new BigDecimal("5000.00"));
        expense1.setExpenseDate(java.time.LocalDate.of(2026, 11, 5));

        Expense expense2 = new Expense();
        expense2.setAmount(new BigDecimal("4000.00"));
        expense2.setExpenseDate(java.time.LocalDate.of(2026, 11, 10));

        Expense expense3 = new Expense();
        expense3.setAmount(new BigDecimal("2000.00"));
        expense3.setExpenseDate(java.time.LocalDate.of(2026, 10, 20));

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.of(budget));

        when(expenseRepository.findAll())
                .thenReturn(List.of(expense1, expense2, expense3));

        BudgetSummaryResponse result =
                budgetService.getBudgetSummary(11, 2026);

        assertNotNull(result);
        assertEquals(new BigDecimal("20000.00"), result.getMonthlyLimit());
        assertEquals(new BigDecimal("9000.00"), result.getTotalSpent());
        assertEquals(new BigDecimal("11000.00"), result.getRemainingBudget());
        assertEquals(new BigDecimal("45.00"), result.getBudgetUtilization());
        assertFalse(result.isExceeded());

        verify(budgetRepository, times(1))
                .findByMonthAndYear(11, 2026);

        verify(expenseRepository, times(1)).findAll();
    }


    // Test 6: Return null when budget does not exist

    @Test
    void getBudgetSummary_ShouldReturnNull_WhenBudgetDoesNotExist() {

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.empty());

        BudgetSummaryResponse result =
                budgetService.getBudgetSummary(11, 2026);

        assertNull(result);

        verify(budgetRepository, times(1))
                .findByMonthAndYear(11, 2026);

        verifyNoInteractions(expenseRepository);
    }


    // Test 7: Handle zero spending

    @Test
    void getBudgetSummary_ShouldHandleZeroSpending() {

        Budget budget = new Budget(
                1L,
                new BigDecimal("20000.00"),
                11,
                2026
        );

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.of(budget));

        when(expenseRepository.findAll())
                .thenReturn(List.of());

        BudgetSummaryResponse result =
                budgetService.getBudgetSummary(11, 2026);

        assertEquals(0, result.getTotalSpent().compareTo(BigDecimal.ZERO));
        assertEquals(new BigDecimal("20000.00"), result.getRemainingBudget());
        assertEquals(0, result.getBudgetUtilization().compareTo(BigDecimal.ZERO));
        assertFalse(result.isExceeded());
    }


    // Test 8: Handle zero monthly budget

    @Test
    void getBudgetSummary_ShouldHandleZeroMonthlyLimit() {

        Budget budget = new Budget(
                1L,
                BigDecimal.ZERO,
                11,
                2026
        );

        when(budgetRepository.findByMonthAndYear(11, 2026))
                .thenReturn(Optional.of(budget));

        when(expenseRepository.findAll())
                .thenReturn(List.of());

        BudgetSummaryResponse result =
                budgetService.getBudgetSummary(11, 2026);

        assertEquals(BigDecimal.ZERO, result.getTotalSpent());
        assertEquals(BigDecimal.ZERO, result.getRemainingBudget());
        assertEquals(BigDecimal.ZERO, result.getBudgetUtilization());
        assertFalse(result.isExceeded());
    }
}
