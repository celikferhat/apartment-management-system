package com.apartment.management.service;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Transaction;
import com.apartment.management.model.User;
import com.apartment.management.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction incomeTransaction;
    private Transaction expenseTransaction;
    private User testUser;
    private Apartment testApartment;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
        
        testApartment = new Apartment();
        testApartment.setId(1L);
        testApartment.setApartmentNumber("101");
        
        incomeTransaction = new Transaction();
        incomeTransaction.setId(1L);
        incomeTransaction.setType(Transaction.TransactionType.INCOME);
        incomeTransaction.setAmount(300.0);
        incomeTransaction.setDate(LocalDate.now());
        incomeTransaction.setCategory(Transaction.Category.DUES);
        incomeTransaction.setDescription("Aidat ödemesi");
        incomeTransaction.setApartment(testApartment);
        incomeTransaction.setCreatedBy(testUser);
        
        expenseTransaction = new Transaction();
        expenseTransaction.setId(2L);
        expenseTransaction.setType(Transaction.TransactionType.EXPENSE);
        expenseTransaction.setAmount(150.0);
        expenseTransaction.setDate(LocalDate.now());
        expenseTransaction.setCategory(Transaction.Category.MAINTENANCE);
        expenseTransaction.setDescription("Bakım gideri");
        expenseTransaction.setCreatedBy(testUser);
    }

    @Test
    public void testGetAllTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(incomeTransaction, expenseTransaction);
        when(transactionRepository.findAll()).thenReturn(expectedTransactions);
        
        // Act
        List<Transaction> actualTransactions = transactionService.getAllTransactions();
        
        // Assert
        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testGetIncomeTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(incomeTransaction);
        when(transactionRepository.findByType(Transaction.TransactionType.INCOME)).thenReturn(expectedTransactions);
        
        // Act
        List<Transaction> actualTransactions = transactionService.getIncomeTransactions();
        
        // Assert
        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepository, times(1)).findByType(Transaction.TransactionType.INCOME);
    }

    @Test
    public void testGetExpenseTransactions() {
        // Arrange
        List<Transaction> expectedTransactions = Arrays.asList(expenseTransaction);
        when(transactionRepository.findByType(Transaction.TransactionType.EXPENSE)).thenReturn(expectedTransactions);
        
        // Act
        List<Transaction> actualTransactions = transactionService.getExpenseTransactions();
        
        // Assert
        assertEquals(expectedTransactions, actualTransactions);
        verify(transactionRepository, times(1)).findByType(Transaction.TransactionType.EXPENSE);
    }

    @Test
    public void testGetTotalIncome() {
        // Arrange
        when(transactionRepository.getTotalIncome()).thenReturn(500.0);
        
        // Act
        Double totalIncome = transactionService.getTotalIncome();
        
        // Assert
        assertEquals(500.0, totalIncome);
        verify(transactionRepository, times(1)).getTotalIncome();
    }

    @Test
    public void testGetTotalExpense() {
        // Arrange
        when(transactionRepository.getTotalExpense()).thenReturn(300.0);
        
        // Act
        Double totalExpense = transactionService.getTotalExpense();
        
        // Assert
        assertEquals(300.0, totalExpense);
        verify(transactionRepository, times(1)).getTotalExpense();
    }

    @Test
    public void testGetCurrentBalance() {
        // Arrange
        when(transactionRepository.getTotalIncome()).thenReturn(500.0);
        when(transactionRepository.getTotalExpense()).thenReturn(300.0);
        
        // Act
        Double currentBalance = transactionService.getCurrentBalance();
        
        // Assert
        assertEquals(200.0, currentBalance);
        verify(transactionRepository, times(1)).getTotalIncome();
        verify(transactionRepository, times(1)).getTotalExpense();
    }

    @Test
    public void testRecordDuePayment() {
        // Arrange
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(3L);
            return savedTransaction;
        });
        
        // Act
        Transaction transaction = transactionService.recordDuePayment(testApartment, 300.0, "Aidat ödemesi", testUser);
        
        // Assert
        assertNotNull(transaction);
        assertEquals(Transaction.TransactionType.INCOME, transaction.getType());
        assertEquals(300.0, transaction.getAmount());
        assertEquals(Transaction.Category.DUES, transaction.getCategory());
        assertEquals("Aidat ödemesi", transaction.getDescription());
        assertEquals(testApartment, transaction.getApartment());
        assertEquals(testUser, transaction.getCreatedBy());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
