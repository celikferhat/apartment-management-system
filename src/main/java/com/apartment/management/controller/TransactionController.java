package com.apartment.management.controller;

import com.apartment.management.model.Apartment;
import com.apartment.management.model.Transaction;
import com.apartment.management.model.User;
import com.apartment.management.service.ApartmentService;
import com.apartment.management.service.TransactionService;
import com.apartment.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final ApartmentService apartmentService;
    private final UserService userService;

    @GetMapping
    public String getAllTransactions(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            List<Transaction> transactions = transactionService.getAllTransactions();
            model.addAttribute("transactions", transactions);
            model.addAttribute("totalIncome", transactionService.getTotalIncome());
            model.addAttribute("totalExpense", transactionService.getTotalExpense());
            model.addAttribute("currentBalance", transactionService.getCurrentBalance());
            
            if (user.isAdmin()) {
                return "transactions/admin-list";
            } else {
                return "transactions/resident-list";
            }
        }
        
        return "redirect:/login";
    }

    @GetMapping("/income")
    public String getIncomeTransactions(Model model) {
        List<Transaction> incomeTransactions = transactionService.getIncomeTransactions();
        model.addAttribute("transactions", incomeTransactions);
        model.addAttribute("totalIncome", transactionService.getTotalIncome());
        model.addAttribute("transactionType", "Gelir");
        return "transactions/list-by-type";
    }

    @GetMapping("/expense")
    public String getExpenseTransactions(Model model) {
        List<Transaction> expenseTransactions = transactionService.getExpenseTransactions();
        model.addAttribute("transactions", expenseTransactions);
        model.addAttribute("totalExpense", transactionService.getTotalExpense());
        model.addAttribute("transactionType", "Gider");
        return "transactions/list-by-type";
    }

    @GetMapping("/{id}")
    public String getTransactionDetails(@PathVariable Long id, Model model) {
        Optional<Transaction> transactionOptional = transactionService.getTransactionById(id);
        
        if (transactionOptional.isPresent()) {
            model.addAttribute("transaction", transactionOptional.get());
            return "transactions/details";
        }
        
        return "redirect:/transactions";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create/income")
    public String createIncomeForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("apartments", apartmentService.getAllApartments());
        model.addAttribute("categories", Transaction.Category.values());
        model.addAttribute("transactionType", Transaction.TransactionType.INCOME);
        return "transactions/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create/expense")
    public String createExpenseForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("apartments", apartmentService.getAllApartments());
        model.addAttribute("categories", Transaction.Category.values());
        model.addAttribute("transactionType", Transaction.TransactionType.EXPENSE);
        return "transactions/create";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createTransaction(@ModelAttribute Transaction transaction, 
                                   @RequestParam(required = false) Long apartmentId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (userOptional.isPresent()) {
            transaction.setCreatedBy(userOptional.get());
            
            if (apartmentId != null) {
                Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(apartmentId);
                apartmentOptional.ifPresent(transaction::setApartment);
            }
            
            if (transaction.getDate() == null) {
                transaction.setDate(LocalDate.now());
            }
            
            transactionService.createTransaction(transaction);
        }
        
        return "redirect:/transactions";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String editTransactionForm(@PathVariable Long id, Model model) {
        Optional<Transaction> transactionOptional = transactionService.getTransactionById(id);
        
        if (transactionOptional.isPresent()) {
            model.addAttribute("transaction", transactionOptional.get());
            model.addAttribute("apartments", apartmentService.getAllApartments());
            model.addAttribute("categories", Transaction.Category.values());
            return "transactions/edit";
        }
        
        return "redirect:/transactions";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/edit")
    public String updateTransaction(@PathVariable Long id, 
                                   @ModelAttribute Transaction transaction,
                                   @RequestParam(required = false) Long apartmentId) {
        Optional<Transaction> existingTransactionOptional = transactionService.getTransactionById(id);
        
        if (existingTransactionOptional.isPresent()) {
            Transaction existingTransaction = existingTransactionOptional.get();
            existingTransaction.setAmount(transaction.getAmount());
            existingTransaction.setDate(transaction.getDate());
            existingTransaction.setCategory(transaction.getCategory());
            existingTransaction.setDescription(transaction.getDescription());
            
            if (apartmentId != null) {
                Optional<Apartment> apartmentOptional = apartmentService.getApartmentById(apartmentId);
                apartmentOptional.ifPresent(existingTransaction::setApartment);
            } else {
                existingTransaction.setApartment(null);
            }
            
            transactionService.updateTransaction(existingTransaction);
        }
        
        return "redirect:/transactions";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/delete")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }

    @GetMapping("/report")
    public String getTransactionReport(Model model) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        
        List<Transaction> monthlyTransactions = transactionService.getTransactionsByDateRange(startOfMonth, now);
        Double monthlyIncome = transactionService.getIncomeByDateRange(startOfMonth, now)
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        Double monthlyExpense = transactionService.getExpenseByDateRange(startOfMonth, now)
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        model.addAttribute("transactions", monthlyTransactions);
        model.addAttribute("monthlyIncome", monthlyIncome);
        model.addAttribute("monthlyExpense", monthlyExpense);
        model.addAttribute("monthlyBalance", monthlyIncome - monthlyExpense);
        model.addAttribute("totalIncome", transactionService.getTotalIncome());
        model.addAttribute("totalExpense", transactionService.getTotalExpense());
        model.addAttribute("currentBalance", transactionService.getCurrentBalance());
        
        return "transactions/report";
    }

    @GetMapping("/report/custom")
    public String getCustomTransactionReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        List<Transaction> customTransactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        Double customIncome = transactionService.getIncomeByDateRange(startDate, endDate)
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        Double customExpense = transactionService.getExpenseByDateRange(startDate, endDate)
                .stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
        
        model.addAttribute("transactions", customTransactions);
        model.addAttribute("customIncome", customIncome);
        model.addAttribute("customExpense", customExpense);
        model.addAttribute("customBalance", customIncome - customExpense);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "transactions/custom-report";
    }
}
