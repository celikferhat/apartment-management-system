package com.apartment.management.repository;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Transaction;
import com.apartment.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByType(Transaction.TransactionType type);
    List<Transaction> findByCategory(Transaction.Category category);
    List<Transaction> findByApartment(Apartment apartment);
    List<Transaction> findByCreatedBy(User user);
    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = 'INCOME'")
    Double getTotalIncome();
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = 'EXPENSE'")
    Double getTotalExpense();
    
    List<Transaction> findByTypeAndDateBetween(Transaction.TransactionType type, LocalDate startDate, LocalDate endDate);
}
