package com.apartment.management.controller;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.User;
import com.apartment.management.service.ApartmentService;
import com.apartment.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/apartments")
@RequiredArgsConstructor
public class ApartmentController {

    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping
    public String getAllApartments(Model model) {
        List<Apartment> apartments = apartmentService.getAllApartments();
        model.addAttribute("apartments", apartments);
        return "apartments/list";
    }

    @GetMapping("/{id}")
    public String getApartmentDetails(@PathVariable Long id, Model model) {
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(id);
        
        if (apartmentOptional.isPresent()) {
            model.addAttribute("apartment", apartmentOptional.get());
            return "apartments/details";
        }
        
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createApartmentForm(Model model) {
        model.addAttribute("apartment", new Apartment());
        model.addAttribute("residents", userService.getAllUsers());
        return "apartments/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createApartment(@ModelAttribute Apartment apartment, @RequestParam(required = false) Long residentId) {
        if (residentId != null) {
            Optional<User> residentOptional = userService.getUserById(residentId);
            residentOptional.ifPresent(apartment::setResident);
            apartment.setOccupied(true);
        } else {
            apartment.setOccupied(false);
        }
        
        apartmentService.createApartment(apartment);
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editApartmentForm(@PathVariable Long id, Model model) {
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(id);
        
        if (apartmentOptional.isPresent()) {
            model.addAttribute("apartment", apartmentOptional.get());
            model.addAttribute("residents", userService.getAllUsers());
            return "apartments/edit";
        }
        
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/edit")
    public String updateApartment(@PathVariable Long id, @ModelAttribute Apartment apartment, @RequestParam(required = false) Long residentId) {
        Optional<Apartment> existingApartmentOptional = apartmentService.getApartmentById(id);
        
        if (existingApartmentOptional.isPresent()) {
            Apartment existingApartment = existingApartmentOptional.get();
            existingApartment.setApartmentNumber(apartment.getApartmentNumber());
            existingApartment.setFloor(apartment.getFloor());
            existingApartment.setNumberOfRooms(apartment.getNumberOfRooms());
            existingApartment.setArea(apartment.getArea());
            
            if (residentId != null) {
                Optional<User> residentOptional = userService.getUserById(residentId);
                if (residentOptional.isPresent()) {
                    existingApartment.setResident(residentOptional.get());
                    existingApartment.setOccupied(true);
                }
            } else {
                existingApartment.setResident(null);
                existingApartment.setOccupied(false);
            }
            
            apartmentService.updateApartment(existingApartment);
        }
        
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteApartment(@PathVariable Long id) {
        apartmentService.deleteApartment(id);
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/assign-resident")
    public String assignResidentForm(@PathVariable Long id, Model model) {
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(id);
        
        if (apartmentOptional.isPresent()) {
            model.addAttribute("apartment", apartmentOptional.get());
            model.addAttribute("residents", userService.getAllUsers());
            return "apartments/assign-resident";
        }
        
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/assign-resident")
    public String assignResident(@PathVariable Long id, @RequestParam Long residentId) {
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(id);
        Optional<User> residentOptional = userService.getUserById(residentId);
        
        if (apartmentOptional.isPresent() && residentOptional.isPresent()) {
            apartmentService.assignResidentToApartment(apartmentOptional.get(), residentOptional.get());
        }
        
        return "redirect:/apartments";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/remove-resident")
    public String removeResident(@PathVariable Long id) {
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(id);
        
        if (apartmentOptional.isPresent()) {
            apartmentService.removeResidentFromApartment(apartmentOptional.get());
        }
        
        return "redirect:/apartments";
    }
}
