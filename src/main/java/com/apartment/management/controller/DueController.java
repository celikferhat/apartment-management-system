package com.apartment.management.controller;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Due;
import com.apartment.management.model.Transaction;
import com.apartment.management.model.User;
import com.apartment.management.service.ApartmentService;
import com.apartment.management.service.DueService;
import com.apartment.management.service.TransactionService;
import com.apartment.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dues")
@RequiredArgsConstructor
public class DueController {

    private final DueService dueService;
    private final ApartmentService apartmentService;
    private final UserService userService;
    private final TransactionService transactionService;

    @GetMapping
    public String getAllDues(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            if (user.isAdmin()) {
                List<Due> dues = dueService.getAllDues();
                model.addAttribute("dues", dues);
                return "dues/admin-list";
            } else {
                List<Apartment> apartments = apartmentService.getApartmentsByResident(user);
                model.addAttribute("apartments", apartments);
                
                if (!apartments.isEmpty()) {
                    List<Due> dues = dueService.getDuesByApartment(apartments.get(0));
                    model.addAttribute("dues", dues);
                }
                
                return "dues/resident-list";
            }
        }
        
        return "redirect:/login";
    }

    @GetMapping("/{id}")
    public String getDueDetails(@PathVariable Long id, Model model) {
        Optional<Due> dueOptional = dueService.getDueById(id);
        
        if (dueOptional.isPresent()) {
            model.addAttribute("due", dueOptional.get());
            return "dues/details";
        }
        
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String createDueForm(Model model) {
        model.addAttribute("due", new Due());
        model.addAttribute("apartments", apartmentService.getAllApartments());
        return "dues/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createDue(@ModelAttribute Due due, @RequestParam Long apartmentId) {
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(apartmentId);
        
        if (apartmentOptional.isPresent()) {
            due.setApartment(apartmentOptional.get());
            dueService.createDue(due);
        }
        
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editDueForm(@PathVariable Long id, Model model) {
        Optional<Due> dueOptional = dueService.getDueById(id);
        
        if (dueOptional.isPresent()) {
            model.addAttribute("due", dueOptional.get());
            model.addAttribute("apartments", apartmentService.getAllApartments());
            return "dues/edit";
        }
        
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/edit")
    public String updateDue(@PathVariable Long id, @ModelAttribute Due due, @RequestParam Long apartmentId) {
        Optional<Due> existingDueOptional = dueService.getDueById(id);
        Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(apartmentId);
        
        if (existingDueOptional.isPresent() && apartmentOptional.isPresent()) {
            Due existingDue = existingDueOptional.get();
            existingDue.setApartment(apartmentOptional.get());
            existingDue.setAmount(due.getAmount());
            existingDue.setDueDate(due.getDueDate());
            existingDue.setPaid(due.isPaid());
            existingDue.setDescription(due.getDescription());
            existingDue.setMonth(due.getMonth());
            existingDue.setYear(due.getYear());
            
            if (due.isPaid() && !existingDue.isPaid()) {
                existingDue.setPaymentDate(LocalDate.now());
            } else {
                existingDue.setPaymentDate(due.getPaymentDate());
            }
            
            dueService.updateDue(existingDue);
        }
        
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteDue(@PathVariable Long id) {
        dueService.deleteDue(id);
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/mark-paid")
    public String markPaidForm(@PathVariable Long id, Model model) {
        Optional<Due> dueOptional = dueService.getDueById(id);
        
        if (dueOptional.isPresent()) {
            model.addAttribute("due", dueOptional.get());
            return "dues/mark-paid";
        }
        
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/mark-paid")
    public String markPaid(@PathVariable Long id) {
        Optional<Due> dueOptional = dueService.getDueById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (dueOptional.isPresent() && userOptional.isPresent()) {
            Due due = dueOptional.get();
            User admin = userOptional.get();
            
            // Mark the due as paid
            Due paidDue = dueService.markAsPaid(id, LocalDate.now());
            
            // Record the payment as a transaction
            String description = "Aidat ödemesi - Daire: " + due.getApartment().getApartmentNumber() + 
                                " - " + due.getMonth() + "/" + due.getYear();
            
            transactionService.recordDuePayment(due.getApartment(), due.getAmount(), description, admin);
        }
        
        return "redirect:/dues";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/generate-monthly")
    public String generateMonthlyDuesForm(Model model) {
        model.addAttribute("apartments", apartmentService.getOccupiedApartments());
        return "dues/generate-monthly";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generate-monthly")
    public String generateMonthlyDues(@RequestParam double amount, 
                                     @RequestParam int month, 
                                     @RequestParam int year, 
                                     @RequestParam String description) {
        List<Apartment> apartments = apartmentService.getOccupiedApartments();
        dueService.generateMonthlyDues(apartments, amount, month, year, description);
        return "redirect:/dues";
    }

    @GetMapping("/whatsapp-link/{id}")
    @ResponseBody
    public String generateWhatsAppLink(@PathVariable Long id) {
        Optional<Due> dueOptional = dueService.getDueById(id);
        
        if (dueOptional.isPresent()) {
            Due due = dueOptional.get();
            User resident = due.getApartment().getResident();
            
            if (resident != null && resident.getPhoneNumber() != null) {
                String phoneNumber = resident.getPhoneNumber().replaceAll("[^0-9]", "");
                String message = "Merhaba " + resident.getName() + ", " + 
                                due.getMonth() + "/" + due.getYear() + 
                                " dönemi aidat ödemeniz (" + due.getAmount() + 
                                " TL) için bu linke tıklayabilirsiniz.";
                
                return "https://wa.me/" + phoneNumber + "?text=" + message.replace(" ", "%20");
            }
        }
        
        return "";
    }
}
