package com.apartment.management.repository;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByResident(User resident);
    List<Apartment> findByOccupied(boolean occupied);
    Apartment findByApartmentNumber(String apartmentNumber);
}
