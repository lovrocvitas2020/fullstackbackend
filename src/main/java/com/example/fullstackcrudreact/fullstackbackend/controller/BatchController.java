package com.example.fullstackcrudreact.fullstackbackend.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/batch")
public class BatchController {

    private static final String REPORTS_DIRECTORY = "C:\\Users\\josip\\Documents\\SpringBootProjects\\TestFolderForProjectClone\\fullstackbackend\\generated_pdfs";

    private final JobLauncher jobLauncher;
    private final Job worklogJob;
    private final Job generatePaymentSlipJob;

    @Autowired
    public BatchController(JobLauncher jobLauncher,
                           @Qualifier("worklogJob") Job worklogJob,
                           @Qualifier("generatePaymentSlipJob") Job generatePaymentSlipJob) {
        this.jobLauncher = jobLauncher;
        this.worklogJob = worklogJob;
        this.generatePaymentSlipJob = generatePaymentSlipJob;
    }

    /**
     *  Method for running batch job for generating Worklogs
     */
    @PostMapping("/startbatch1")
    public ResponseEntity<Map<String, Object>> startBatchJob1() {

        System.out.println("startBatchJob1 START ");

        try {

            System.out.println("startBatchJob1 run - batch1:  worklogJob");


            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(worklogJob, jobParameters);
            Thread.sleep(3000); // Wait for the batch job to complete

            Path reportsDir = Paths.get(REPORTS_DIRECTORY);

            Optional<Path> latestFile = Files.list(reportsDir)
                    .filter(Files::isRegularFile)
                    .map(Path.class::cast)
                    .sorted(Comparator.comparingLong(path -> ((Path) path).toFile().lastModified()).reversed())
                    .findFirst();

            if (latestFile.isPresent()) {
                File file = latestFile.get().toFile();

                // Convert file to byte array (blob)
                byte[] fileContent = FileUtils.readFileToByteArray(file);

                // Build the response with the file's blob and metadata
                Map<String, Object> responseBody = Map.of(
                        "fileName", file.getName(),
                        "lastModified", file.lastModified(),
                        "size", file.length(),
                        "blob", fileContent
                );

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No report file found."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error starting batch job: " + e.getMessage()));
        }
    }

    /**
     *  Method for running batch job for generating payment slips
     */
    @PostMapping("/startbatch2")
    public ResponseEntity<List<Map<String, Object>>> startBatchJob2() {

        System.out.println("startBatchJob2 START generatePaymentSlipJob ");

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(generatePaymentSlipJob, jobParameters);
            Thread.sleep(3000); // Wait for the batch job to complete


            // Path where the payment slip files are stored
            Path reportsDir = Paths.get(REPORTS_DIRECTORY);
    
            // Read all files from the directory
            List<Path> paymentSlipFiles = Files.list(reportsDir)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
    
            // Process each file and create a response with its metadata
            List<Map<String, Object>> responseFiles = paymentSlipFiles.stream()
                    .map(path -> {
                        try {
                            File file = path.toFile();
                            byte[] fileContent = FileUtils.readFileToByteArray(file);
    
                            // Create a mutable Map to store the file details
                            Map<String, Object> fileDetails = new HashMap<>();
                            fileDetails.put("fileName", file.getName());
                           // fileDetails.put("lastModified", file.lastModified());
                           // fileDetails.put("size", file.length());
                           // fileDetails.put("blob", fileContent);
    
                            return fileDetails;
                        } catch (Exception e) {
                            // In case of error, return a map with an error message
                            Map<String, Object> errorDetails = new HashMap<>();
                            errorDetails.put("error", "Error reading file " + path.getFileName());
                            return errorDetails;
                        }
                    })
                    .collect(Collectors.toList());
    
            // Return ResponseEntity with the List of Maps
            return ResponseEntity.ok(responseFiles);
    
        } catch (Exception e) {
        // Wrap error response in a List to match the method signature
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("error", "Error processing the batch job: " + e.getMessage());

        // Return a list containing the error message
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(errorDetails));
        }
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {

        System.out.println("getFile method");

        try {
            Path filePath = Paths.get(REPORTS_DIRECTORY).resolve(filename);
            File file = filePath.toFile();

            if (file.exists() && file.isFile()) {
                FileSystemResource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
