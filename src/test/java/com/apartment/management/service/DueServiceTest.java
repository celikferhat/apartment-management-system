package com.apartment.management.service;

import com.apartment.management.model.Due;
import com.apartment.management.model.User;
import com.apartment.management.repository.DueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DueServiceTest {

    @Mock
    private DueRepository dueRepository;

    @InjectMocks
    private DueService dueService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllDues() {
        // Arrange
        Due due1 = new Due();
        Due due2 = new Due();
        List<Due> expectedDues = Arrays.asList(due1, due2);
        
        when(dueRepository.findAll()).thenReturn(expectedDues);
        
        // Act
        List<Due> actualDues = dueService.getAllDues();
        
        // Assert
        assertEquals(expectedDues, actualDues);
        verify(dueRepository, times(1)).findAll();
    }

    @Test
    public void testGetDueById() {
        // Arrange
        Due expectedDue = new Due();
        expectedDue.setId(1L);
        
        when(dueRepository.findById(1L)).thenReturn(Optional.of(expectedDue));
        
        // Act
        Optional<Due> actualDue = dueService.getDueById(1L);
        
        // Assert
        assertTrue(actualDue.isPresent());
        assertEquals(expectedDue, actualDue.get());
        verify(dueRepository, times(1)).findById(1L);
    }

    @Test
    public void testMarkAsPaid() {
        // Arrange
        Due due = new Due();
        due.setId(1L);
        due.setPaid(false);
        
        when(dueRepository.findById(1L)).thenReturn(Optional.of(due));
        when(dueRepository.save(any(Due.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        LocalDate paymentDate = LocalDate.now();
        
        // Act
        Due updatedDue = dueService.markAsPaid(1L, paymentDate);
        
        // Assert
        assertTrue(updatedDue.isPaid());
        assertEquals(paymentDate, updatedDue.getPaymentDate());
        verify(dueRepository, times(1)).findById(1L);
        verify(dueRepository, times(1)).save(due);
    }

    @Test
    public void testMarkAsPaidWithNonExistingDue() {
        // Arrange
        when(dueRepository.findById(1L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> dueService.markAsPaid(1L, LocalDate.now()));
        verify(dueRepository, times(1)).findById(1L);
        verify(dueRepository, never()).save(any(Due.class));
    }
}
