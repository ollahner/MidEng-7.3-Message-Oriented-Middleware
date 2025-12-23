package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Service
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final List<String> storageReport = new ArrayList<>();

    @KafkaListener(topics = "warehouse-data", groupId = "central-office-group")
    public void processMessage(String content) {
        logger.info("Received Message: " + content);

        synchronized (storageReport) {
            storageReport.add(content);
        }

        System.out.println("Log: " + content);
    }

    @GetMapping("/management/report")
    public List<String> getManagementReport() {
        return storageReport;
    }

}