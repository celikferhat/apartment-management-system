package com.apartment.management.service;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Due;
import com.apartment.management.repository.DueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DueService {

    private final DueRepository dueRepository;

    public List<Due> getAllDues() {
        return dueRepository.findAll();
    }

    public Optional<Due> getDueById(Long id) {
        return dueRepository.findById(id);
    }

    public List<Due> getDuesByApartment(Apartment apartment) {
        return dueRepository.findByApartment(apartment);
    }

    public List<Due> getPaidDues() {
        return dueRepository.findByPaid(true);
    }

    public List<Due> getUnpaidDues() {
        return dueRepository.findByPaid(false);
    }

    public List<Due> getUnpaidDuesByApartment(Apartment apartment) {
        return dueRepository.findByApartmentAndPaid(apartment, false);
    }

    public List<Due> getDuesByMonthAndYear(int month, int year) {
        return dueRepository.findByMonthAndYear(month, year);
    }

    public List<Due> getOverdueDues() {
        return dueRepository.findByDueDateBefore(LocalDate.now());
    }

    public Due createDue(Due due) {
        return dueRepository.save(due);
    }

    public Due updateDue(Due due) {
        return dueRepository.save(due);
    }

    public void deleteDue(Long id) {
        dueRepository.deleteById(id);
    }

    public Due markAsPaid(Long id, LocalDate paymentDate) {
        Due due = dueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Due not found with id: " + id));
        due.setPaid(true);
        due.setPaymentDate(paymentDate);
        return dueRepository.save(due);
    }

    public void generateMonthlyDues(List<Apartment> apartments, double amount, int month, int year, String description) {
        LocalDate dueDate = LocalDate.of(year, month, 15);
        
        for (Apartment apartment : apartments) {
            Due due = new Due();
            due.setApartment(apartment);
            due.setAmount(amount);
            due.setDueDate(dueDate);
            due.setPaid(false);
            due.setDescription(description);
            due.setMonth(month);
            due.setYear(year);
            dueRepository.save(due);
        }
    }
}
