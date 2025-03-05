package com.example.fullstackcrudreact.fullstackbackend.batch.batch1;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {


    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    public BatchConfig(JobRepository jobRepository, 
                      PlatformTransactionManager transactionManager, 
                      DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
    }

    // Fixed SQL query and column mappings
    @Bean
    public JdbcCursorItemReader<Worklog> worklogReader() {
        return new JdbcCursorItemReaderBuilder<Worklog>()
                .name("worklogReader")
                .dataSource(dataSource)
                .sql("SELECT w.id, w.end_hour, w.start_hour, w.work_date, w.work_description, u.name, w.hour_sum FROM worklog w " +
                    "inner join user u on u.id = w.user_id ")
                .rowMapper(new WorklogRowMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Worklog, Worklog> worklogProcessor() {
        return worklog -> worklog;
    }

    @Bean
    public ItemWriter<Worklog> worklogWriter() {
        return new WorklogWriter();
    }

    @Bean
    public Step worklogStep() {
        return new StepBuilder("worklogStep", jobRepository)
                .<Worklog, Worklog>chunk(10, transactionManager)
                .reader(worklogReader())
                .processor(worklogProcessor())
                .writer(worklogWriter())
                .build();
    }

    @Bean
    public Job worklogJob() {
        return new JobBuilder("worklogJob", jobRepository)
                .start(worklogStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    public static class WorklogRowMapper implements RowMapper<Worklog> {
        @Override
        public Worklog mapRow(ResultSet rs, int rowNum) throws SQLException {
            Worklog worklog = new Worklog();
            worklog.setId(rs.getLong("id"));
            worklog.setStartHour(rs.getTime("start_hour").toLocalTime());
            worklog.setEndHour(rs.getTime("end_hour").toLocalTime());
            worklog.setWorkDate(rs.getDate("work_date").toLocalDate());
            worklog.setWorkDescription(rs.getString("work_description"));
            worklog.setName(rs.getString("name"));

            Long totalSeconds = rs.getLong("hour_sum");
            Long totalHours = null;

            int number = 10;
          
            if (totalSeconds > 0) {
                totalHours = totalSeconds/3600;
            } else {
              
                totalHours = (long) 0;
            }
           
            worklog.setHourSum(totalHours);

            return worklog;
        }
    }



}
