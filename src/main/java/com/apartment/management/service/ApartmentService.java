package com.apartment.management.service;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.User;
import com.apartment.management.repository.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApartmentService {

    private final ApartmentRepository apartmentRepository;

    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    public Optional<Apartment> getApartmentById(Long id) {
        return apartmentRepository.findById(id);
    }

    public List<Apartment> getApartmentsByResident(User resident) {
        return apartmentRepository.findByResident(resident);
    }

    public List<Apartment> getOccupiedApartments() {
        return apartmentRepository.findByOccupied(true);
    }

    public List<Apartment> getVacantApartments() {
        return apartmentRepository.findByOccupied(false);
    }

    public Apartment getApartmentByNumber(String apartmentNumber) {
        return apartmentRepository.findByApartmentNumber(apartmentNumber);
    }

    public Apartment createApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    public Apartment updateApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }

    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
    }

    public void assignResidentToApartment(Apartment apartment, User resident) {
        apartment.setResident(resident);
        apartment.setOccupied(true);
        apartmentRepository.save(apartment);
    }

    public void removeResidentFromApartment(Apartment apartment) {
        apartment.setResident(null);
        apartment.setOccupied(false);
        apartmentRepository.save(apartment);
    }
}
