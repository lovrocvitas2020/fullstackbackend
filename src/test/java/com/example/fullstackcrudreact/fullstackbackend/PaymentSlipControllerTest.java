package com.example.fullstackcrudreact.fullstackbackend;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.fullstackcrudreact.fullstackbackend.controller.PaymentSlipController;
import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.PaymentSlipRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PaymentSlipControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentSlipRepository paymentSlipRepository;

    @InjectMocks
    private PaymentSlipController paymentSlipController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(paymentSlipController).build();
    }

     @Test
    void testGetAllPaymentSlips() throws Exception {
        PaymentSlip slip1 = new PaymentSlip();
        PaymentSlip slip2 = new PaymentSlip();
        when(paymentSlipRepository.findAll()).thenReturn(Arrays.asList(slip1, slip2));

        mockMvc.perform(get("/viewpaymentslips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetPaymentSlipById_Found() throws Exception {
        PaymentSlip slip = new PaymentSlip();
        slip.setId(1L);
        when(paymentSlipRepository.findById(1L)).thenReturn(Optional.of(slip));

        mockMvc.perform(get("/viewpaymentslip/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetPaymentSlipById_NotFound() throws Exception {
        when(paymentSlipRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/viewpaymentslip/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePaymentSlip_Success() throws Exception {
        PaymentSlip slip = new PaymentSlip();
        slip.setId(1L);
        when(paymentSlipRepository.save(any(PaymentSlip.class))).thenReturn(slip);

        mockMvc.perform(post("/addpaymentslips")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slip)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdatePaymentSlip_Success() throws Exception {
        PaymentSlip existingSlip = new PaymentSlip();
        existingSlip.setId(1L);

        PaymentSlip updatedSlip = new PaymentSlip();
        updatedSlip.setId(1L);
        updatedSlip.setAmount("100.00");

        when(paymentSlipRepository.findById(1L)).thenReturn(Optional.of(existingSlip));
        when(paymentSlipRepository.save(any(PaymentSlip.class))).thenReturn(updatedSlip);

        mockMvc.perform(put("/editpaymentslip/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSlip)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value("100.00"));
    }

    @Test
    void testUpdatePaymentSlip_NotFound() throws Exception {
        PaymentSlip updatedSlip = new PaymentSlip();
        updatedSlip.setId(1L);

        when(paymentSlipRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/editpaymentslip/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedSlip)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePaymentSlip_Success() throws Exception {
        PaymentSlip slip = new PaymentSlip();
        slip.setId(1L);

        when(paymentSlipRepository.findById(1L)).thenReturn(Optional.of(slip));
        doNothing().when(paymentSlipRepository).delete(slip);

        mockMvc.perform(delete("/deletepaymentslip/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePaymentSlip_NotFound() throws Exception {
        when(paymentSlipRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/deletepaymentslip/1"))
                .andExpect(status().isNotFound());
    }

}
