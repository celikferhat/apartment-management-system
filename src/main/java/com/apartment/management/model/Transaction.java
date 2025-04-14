package com.apartment.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    
    public enum TransactionType {
        INCOME, EXPENSE
    }
    
    public enum Category {
        DUES, MAINTENANCE, UTILITY, REPAIR, CLEANING, SALARY, OTHER
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false)
    private double amount;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    
    @Column
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
}
