package com.example.fullstackcrudreact.fullstackbackend.batch.batch2;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;
import com.example.fullstackcrudreact.fullstackbackend.repository.PaymentSlipRepository;

@Component
public class PaymentSlipItemReader implements ItemReader<PaymentSlip> {

    private final PaymentSlipRepository paymentSlipRepository;
    private Iterator<PaymentSlip> paymentSlipIterator;

    public PaymentSlipItemReader(PaymentSlipRepository paymentSlipRepository) {
        this.paymentSlipRepository = paymentSlipRepository;
        this.paymentSlipIterator = fetchData();
    }

    private Iterator<PaymentSlip> fetchData() {
        List<PaymentSlip> paymentSlips = paymentSlipRepository.findAll(); // Fetch all payment slips
        return paymentSlips.iterator();
    }

    @Override
    @Nullable
    public PaymentSlip read()
            throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                if (paymentSlipIterator == null || !paymentSlipIterator.hasNext()) {
                    paymentSlipIterator = fetchData(); // Re-fetch data for the next batch run
                    return null; // Return null to indicate the end of the batch step
                }
                return paymentSlipIterator.next(); // Return the next PaymentSlip
    }

}
