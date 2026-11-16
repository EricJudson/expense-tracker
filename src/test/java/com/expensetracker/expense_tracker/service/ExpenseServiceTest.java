package com.expensetracker.expense_tracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.dto.ExpenseSummaryResponse;
import com.expensetracker.expense_tracker.entity.Expense;
import com.expensetracker.expense_tracker.enums.ExpenseCategory;
import com.expensetracker.expense_tracker.exception.ExpenseNotFoundException;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;


@Test
void getAllExpenses_ShouldReturnAllExpenses() {

    // Arrange
    Expense expense1 = new Expense();
    Expense expense2 = new Expense();

    when(expenseRepository.findAll())
            .thenReturn(List.of(expense1, expense2));

    // Act
    List<ExpenseResponse> result = expenseService.getAllExpenses();

    // Assert
    assertEquals(2, result.size());
    verify(expenseRepository, times(1)).findAll();
}

@Test
void createExpense_ShouldSaveAndReturnExpense() {

    // Arrange
    ExpenseRequest request = new ExpenseRequest();
    request.setTitle("Lunch");
    request.setAmount(new BigDecimal("250.00"));
    request.setDescription("Lunch with friends");
    request.setExpenseDate(LocalDate.of(2026, 11, 16));
    request.setCategory(ExpenseCategory.FOOD);

    Expense savedExpense = new Expense();
    savedExpense.setId(1L);
    savedExpense.setTitle("Lunch");
    savedExpense.setAmount(new BigDecimal("250.00"));
    savedExpense.setDescription("Lunch with friends");
    savedExpense.setExpenseDate(LocalDate.of(2026, 11, 16));
    savedExpense.setCategory(ExpenseCategory.FOOD);

    when(expenseRepository.save(any(Expense.class)))
            .thenReturn(savedExpense);

    // Act
    ExpenseResponse result = expenseService.createExpense(request);

    // Assert
    assertEquals(1L, result.getId());
    assertEquals("Lunch", result.getTitle());
    assertEquals(new BigDecimal("250.00"), result.getAmount());
    assertEquals(ExpenseCategory.FOOD, result.getCategory());

    verify(expenseRepository, times(1)).save(any(Expense.class));
}


@Test
void getExpenseById_ShouldReturnExpense_WhenExpenseExists() {

    // Arrange
    Expense expense = new Expense();
    expense.setId(1L);
    expense.setTitle("Groceries");
    expense.setAmount(new BigDecimal("500.00"));
    expense.setDescription("Weekly groceries");
    expense.setExpenseDate(LocalDate.of(2026, 11, 16));
    expense.setCategory(ExpenseCategory.FOOD);

    when(expenseRepository.findById(1L))
            .thenReturn(Optional.of(expense));

    // Act
    ExpenseResponse result = expenseService.getExpenseById(1L);

    // Assert
    assertEquals(1L, result.getId());
    assertEquals("Groceries", result.getTitle());
    assertEquals(new BigDecimal("500.00"), result.getAmount());

    verify(expenseRepository, times(1)).findById(1L);
}


@Test
void getExpenseById_ShouldThrowException_WhenExpenseDoesNotExist() {

    // Arrange
    when(expenseRepository.findById(99L))
            .thenReturn(Optional.empty());

    // Act & Assert
        ExpenseNotFoundException exception = assertThrows(
            ExpenseNotFoundException.class,
            () -> expenseService.getExpenseById(99L)
    );

    verify(expenseRepository, times(1)).findById(99L);
}


@Test
void updateExpense_ShouldUpdateExistingExpense() {

    // Arrange
    Expense existingExpense = new Expense();
    existingExpense.setId(1L);
    existingExpense.setTitle("Lunch");
    existingExpense.setAmount(new BigDecimal("200.00"));
    existingExpense.setDescription("Old description");
    existingExpense.setExpenseDate(LocalDate.of(2026, 11, 16));
    existingExpense.setCategory(ExpenseCategory.FOOD);

    ExpenseRequest request = new ExpenseRequest();
    request.setTitle("Dinner");
    request.setAmount(new BigDecimal("450.00"));
    request.setDescription("Updated description");
    request.setExpenseDate(LocalDate.of(2026, 11, 16));
    request.setCategory(ExpenseCategory.FOOD);

    when(expenseRepository.findById(1L))
            .thenReturn(Optional.of(existingExpense));

    when(expenseRepository.save(any(Expense.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    ExpenseResponse result = expenseService.updateExpense(1L, request);

    // Assert
    assertEquals("Dinner", result.getTitle());
    assertEquals(new BigDecimal("450.00"), result.getAmount());
    assertEquals("Updated description", result.getDescription());

    verify(expenseRepository, times(1)).findById(1L);
    verify(expenseRepository, times(1)).save(any(Expense.class));
}


@Test
void deleteExpense_ShouldDeleteExpense_WhenExpenseExists() {

    // Arrange
    when(expenseRepository.existsById(1L)).thenReturn(true);

    // Act
    boolean result = expenseService.deleteExpense(1L);

    // Assert
    assertTrue(result);
    verify(expenseRepository, times(1)).existsById(1L);
    verify(expenseRepository, times(1)).deleteById(1L);
}


@Test
void deleteExpense_ShouldNotDelete_WhenExpenseDoesNotExist() {

    // Arrange
    when(expenseRepository.existsById(99L)).thenReturn(false);

    // Act
    boolean result = expenseService.deleteExpense(99L);

    // Assert
    assertFalse(result);
    verify(expenseRepository, times(1)).existsById(99L);
    verify(expenseRepository, never()).deleteById(99L);
}


@Test
void getExpensesByCategory_ShouldReturnMatchingExpenses() {

    // Arrange
    Expense expense1 = new Expense();
    expense1.setId(1L);
    expense1.setTitle("Lunch");
    expense1.setAmount(new BigDecimal("250.00"));
    expense1.setCategory(ExpenseCategory.FOOD);

    Expense expense2 = new Expense();
    expense2.setId(2L);
    expense2.setTitle("Dinner");
    expense2.setAmount(new BigDecimal("400.00"));
    expense2.setCategory(ExpenseCategory.FOOD);

    when(expenseRepository.findByCategory(ExpenseCategory.FOOD))
            .thenReturn(List.of(expense1, expense2));

    // Act
    List<ExpenseResponse> result =
            expenseService.getExpensesByCategory(ExpenseCategory.FOOD);

    // Assert
    assertEquals(2, result.size());
    assertEquals("Lunch", result.get(0).getTitle());
    assertEquals("Dinner", result.get(1).getTitle());

    verify(expenseRepository, times(1))
            .findByCategory(ExpenseCategory.FOOD);
}


@Test
void getExpenseSummary_ShouldCalculateCorrectly() {

    // Arrange
    Expense expense1 = new Expense();
    expense1.setAmount(new BigDecimal("200.00"));

    Expense expense2 = new Expense();
    expense2.setAmount(new BigDecimal("500.00"));

    Expense expense3 = new Expense();
    expense3.setAmount(new BigDecimal("300.00"));

    when(expenseRepository.findAll())
            .thenReturn(List.of(expense1, expense2, expense3));

    // Act
    ExpenseSummaryResponse result = expenseService.getExpenseSummary();

    // Assert
    assertEquals(3L, result.getTotalExpenses());
    assertEquals(new BigDecimal("1000.00"), result.getTotalAmount());
    assertEquals(new BigDecimal("333.33"), result.getAverageExpense());
    assertEquals(new BigDecimal("500.00"), result.getHighestExpense());
    assertEquals(new BigDecimal("200.00"), result.getLowestExpense());

    verify(expenseRepository, times(1)).findAll();
}
}

