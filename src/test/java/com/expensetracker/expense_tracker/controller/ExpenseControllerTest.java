
package com.expensetracker.expense_tracker.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.expensetracker.expense_tracker.dto.ExpenseRequest;
import com.expensetracker.expense_tracker.dto.ExpenseResponse;
import com.expensetracker.expense_tracker.dto.ExpenseSummaryResponse;
import com.expensetracker.expense_tracker.dto.MonthlySpendingResponse;
import com.expensetracker.expense_tracker.enums.ExpenseCategory;
import com.expensetracker.expense_tracker.exception.ExpenseNotFoundException;
import com.expensetracker.expense_tracker.service.ExpenseService;


@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService expenseService;


    // Helper method to create sample ExpenseResponse

    private ExpenseResponse createExpenseResponse() {

        ExpenseResponse response = new ExpenseResponse();

        response.setId(1L);
        response.setTitle("Lunch");
        response.setDescription("Lunch with friends");
        response.setAmount(new BigDecimal("250.00"));
        response.setExpenseDate(LocalDate.of(2026, 11, 16));
        response.setCreatedAt(LocalDateTime.of(2026, 11, 16, 12, 0));
        response.setCategory(ExpenseCategory.FOOD);

        return response;
    }


    // Test 1: Get all expenses

    @Test
    void getAllExpenses_ShouldReturnAllExpenses() throws Exception {

        ExpenseResponse expense = createExpenseResponse();

        when(expenseService.getAllExpenses())
                .thenReturn(List.of(expense));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Lunch"))
                .andExpect(jsonPath("$[0].amount").value(250.00));

        verify(expenseService, times(1)).getAllExpenses();
    }


    // Test 2: Create an expense

    @Test
    void createExpense_ShouldReturnCreatedExpense() throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Lunch");
        request.setAmount(new BigDecimal("250.00"));
        request.setDescription("Lunch with friends");
        request.setExpenseDate(LocalDate.of(2026, 11, 16));
        request.setCategory(ExpenseCategory.FOOD);

        ExpenseResponse response = createExpenseResponse();

        when(expenseService.createExpense(any(ExpenseRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Lunch\",\"amount\":250.00,\"description\":\"Lunch with friends\",\"expenseDate\":\"2026-11-16\",\"category\":\"FOOD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.category").value("FOOD"));

        verify(expenseService, times(1))
                .createExpense(any(ExpenseRequest.class));
    }


    // Test 3: Get expense by ID

    @Test
    void getExpenseById_ShouldReturnExpense() throws Exception {

        ExpenseResponse response = createExpenseResponse();

        when(expenseService.getExpenseById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(250.00));

        verify(expenseService, times(1)).getExpenseById(1L);
    }


    // Test 4: Get expense by ID when it does not exist

    @Test
    void getExpenseById_ShouldReturnNotFound() throws Exception {

        when(expenseService.getExpenseById(99L))
                .thenThrow(new ExpenseNotFoundException("Expense not found"));

        mockMvc.perform(get("/api/expenses/99"))
                .andExpect(status().isNotFound());

        verify(expenseService, times(1)).getExpenseById(99L);
    }


    // Test 5: Update an expense

    @Test
    void updateExpense_ShouldReturnUpdatedExpense() throws Exception {

        ExpenseRequest request = new ExpenseRequest();

        request.setTitle("Dinner");
        request.setAmount(new BigDecimal("450.00"));
        request.setDescription("Dinner with friends");
        request.setExpenseDate(LocalDate.of(2026, 11, 16));
        request.setCategory(ExpenseCategory.FOOD);

        ExpenseResponse response = createExpenseResponse();

        response.setTitle("Dinner");
        response.setAmount(new BigDecimal("450.00"));
        response.setDescription("Dinner with friends");

        when(expenseService.updateExpense(eq(1L), any(ExpenseRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Dinner\",\"amount\":450.00,\"description\":\"Dinner with friends\",\"expenseDate\":\"2026-11-16\",\"category\":\"FOOD\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dinner"))
                .andExpect(jsonPath("$.amount").value(450.00));

        verify(expenseService, times(1))
                .updateExpense(eq(1L), any(ExpenseRequest.class));
    }


    // Test 6: Delete an expense

    @Test
    void deleteExpense_ShouldReturnNoContent() throws Exception {

        when(expenseService.deleteExpense(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).deleteExpense(1L);
    }


    // Test 7: Get expenses by category

    @Test
    void getExpensesByCategory_ShouldReturnMatchingExpenses() throws Exception {

        ExpenseResponse response = createExpenseResponse();

        when(expenseService.getExpensesByCategory(ExpenseCategory.FOOD))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/expenses/category/FOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].category").value("FOOD"));

        verify(expenseService, times(1))
                .getExpensesByCategory(ExpenseCategory.FOOD);
    }


    // Test 8: Get expense summary

    @Test
    void getExpenseSummary_ShouldReturnSummary() throws Exception {

        ExpenseSummaryResponse summary = new ExpenseSummaryResponse(
                5L,
                new BigDecimal("12500.00"),
                new BigDecimal("2500.00"),
                new BigDecimal("5000.00"),
                new BigDecimal("500.00")
        );

        when(expenseService.getExpenseSummary())
                .thenReturn(summary);

        mockMvc.perform(get("/api/expenses/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExpenses").value(5))
                .andExpect(jsonPath("$.totalAmount").value(12500.00))
                .andExpect(jsonPath("$.averageExpense").value(2500.00))
                .andExpect(jsonPath("$.highestExpense").value(5000.00))
                .andExpect(jsonPath("$.lowestExpense").value(500.00));

        verify(expenseService, times(1)).getExpenseSummary();
    }


    // Test 9: Get monthly spending analytics

    @Test
    void getMonthlySpending_ShouldReturnMonthlyData() throws Exception {

        MonthlySpendingResponse monthlyResponse =
                new MonthlySpendingResponse(
                        2026,
                        11,
                        5L,
                        new BigDecimal("7500.00")
                );

        when(expenseService.getMonthlySpending())
                .thenReturn(List.of(monthlyResponse));

        mockMvc.perform(get("/api/expenses/monthly-spending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].year").value(2026))
                .andExpect(jsonPath("$[0].month").value(11))
                .andExpect(jsonPath("$[0].totalExpenses").value(5))
                .andExpect(jsonPath("$[0].totalAmount").value(7500.00));

        verify(expenseService, times(1)).getMonthlySpending();
    }

}
