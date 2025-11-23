package com.example.LibraryManagement.librarymanagement.worker;

import com.example.LibraryManagement.librarymanagement.Service.EmailSendingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.Map;

@Component
public class OtpQueueWorker {

    @Value("${aws.sqs.queue-url}")
    private String queueUrl;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private EmailSendingService emailSendingService;

    private ObjectMapper mapper = new ObjectMapper();

    @Scheduled(fixedDelay = 3000)
    public void PollQueue(){

        ReceiveMessageResponse response = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .waitTimeSeconds(20)
                .messageAttributeNames("All")
                .build()
        );

        try{
            if(response==null||response.messages().isEmpty()) return;
            for(Message message : response.messages()){
                process(message);
                delete(message);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void delete(Message message){
        sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(queueUrl).receiptHandle(message.receiptHandle()).build());
    }

    private void process(Message messg) throws JsonProcessingException {

        try{
            Map<String, Object> data = mapper.readValue(messg.body(),Map.class);
            String email = (String) data.get("email").toString();
            String message = (String) data.get("message").toString();

            emailSendingService.sendEmail(email,"Verification OTP", message);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
