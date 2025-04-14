package com.apartment.management.service;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Transaction;
import com.apartment.management.model.User;
import com.apartment.management.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getIncomeTransactions() {
        return transactionRepository.findByType(Transaction.TransactionType.INCOME);
    }

    public List<Transaction> getExpenseTransactions() {
        return transactionRepository.findByType(Transaction.TransactionType.EXPENSE);
    }

    public List<Transaction> getTransactionsByCategory(Transaction.Category category) {
        return transactionRepository.findByCategory(category);
    }

    public List<Transaction> getTransactionsByApartment(Apartment apartment) {
        return transactionRepository.findByApartment(apartment);
    }

    public List<Transaction> getTransactionsByCreator(User user) {
        return transactionRepository.findByCreatedBy(user);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByDateBetween(startDate, endDate);
    }

    public List<Transaction> getIncomeByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTypeAndDateBetween(Transaction.TransactionType.INCOME, startDate, endDate);
    }

    public List<Transaction> getExpenseByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByTypeAndDateBetween(Transaction.TransactionType.EXPENSE, startDate, endDate);
    }

    public Double getTotalIncome() {
        return transactionRepository.getTotalIncome() != null ? transactionRepository.getTotalIncome() : 0.0;
    }

    public Double getTotalExpense() {
        return transactionRepository.getTotalExpense() != null ? transactionRepository.getTotalExpense() : 0.0;
    }

    public Double getCurrentBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public Transaction recordDuePayment(Apartment apartment, double amount, String description, User createdBy) {
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(amount);
        transaction.setDate(LocalDate.now());
        transaction.setCategory(Transaction.Category.DUES);
        transaction.setDescription(description);
        transaction.setApartment(apartment);
        transaction.setCreatedBy(createdBy);
        return transactionRepository.save(transaction);
    }

    public Transaction recordOtherIncome(double amount, String description, User createdBy) {
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.INCOME);
        transaction.setAmount(amount);
        transaction.setDate(LocalDate.now());
        transaction.setCategory(Transaction.Category.OTHER);
        transaction.setDescription(description);
        transaction.setCreatedBy(createdBy);
        return transactionRepository.save(transaction);
    }

    public Transaction recordExpense(double amount, Transaction.Category category, String description, User createdBy) {
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TransactionType.EXPENSE);
        transaction.setAmount(amount);
        transaction.setDate(LocalDate.now());
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setCreatedBy(createdBy);
        return transactionRepository.save(transaction);
    }
}
