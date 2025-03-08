package com.example.fullstackcrudreact.fullstackbackend.batch.batch2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.fullstackcrudreact.fullstackbackend.model.PaymentSlip;


@Configuration
@EnableBatchProcessing
@EnableScheduling
public class PaymentSlipBatchConfig {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSlipBatchConfig.class);



    @Bean
    public Job generatePaymentSlipJob(JobRepository jobRepository, Step generatePaymentSlipStep) {
        return new JobBuilder("generatePaymentSlipJob", jobRepository)
                .start(generatePaymentSlipStep)
                .build();
    }

     @Bean
    public Step generatePaymentSlipStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                        ItemReader<PaymentSlip> reader, ItemProcessor<PaymentSlip, PaymentSlip> processor,
                                        ItemWriter<PaymentSlip> writer) {
        return new StepBuilder("generatePaymentSlipStep", jobRepository)
                .<PaymentSlip, PaymentSlip>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void runBatchJob() {
        logger.info("Starting PaymentSlip Batch Job");
        try {
            // JobLauncher must be autowired or obtained via a custom bean
            // jobLauncher.run(generatePaymentSlipJob, jobParameters);
        } catch (Exception e) {
            logger.error("Error running batch job", e);
        }
    }

   

   


}
