package com.apartment.management.repository;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Due;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DueRepository extends JpaRepository<Due, Long> {
    List<Due> findByApartment(Apartment apartment);
    List<Due> findByPaid(boolean paid);
    List<Due> findByApartmentAndPaid(Apartment apartment, boolean paid);
    List<Due> findByMonthAndYear(int month, int year);
    List<Due> findByDueDateBefore(LocalDate date);
}
