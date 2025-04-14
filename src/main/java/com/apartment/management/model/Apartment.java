package com.apartment.management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "apartments")
public class Apartment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String apartmentNumber;
    
    @Column
    private int floor;
    
    @Column
    private int numberOfRooms;
    
    @Column
    private double area;
    
    @ManyToOne
    @JoinColumn(name = "resident_id")
    private User resident;
    
    @Column
    private boolean occupied = true;
}
