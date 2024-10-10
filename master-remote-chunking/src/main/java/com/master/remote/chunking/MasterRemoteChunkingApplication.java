package com.master.remote.chunking;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableBatchProcessing
@EnableBatchIntegration
@EnableIntegration
public class MasterRemoteChunkingApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(MasterRemoteChunkingApplication.class, args);
    }

}
