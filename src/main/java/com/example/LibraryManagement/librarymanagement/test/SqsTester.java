//package com.example.LibraryManagement.librarymanagement.test;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Component;
//import software.amazon.awssdk.services.sqs.SqsClient;
//import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
//
//@Component
//public class SqsTester {
//
//    private final SqsClient sqsClient;
//
//    public SqsTester(SqsClient sqsClient) {
//        this.sqsClient = sqsClient;
//    }
//
//    @PostConstruct
//    public void test() {
//        try {
//            var resp = sqsClient.sendMessage(
//                    SendMessageRequest.builder()
//                            .queueUrl("https://sqs.ap-south-1.amazonaws.com/518286664388/LibraryManagementOTPQueue")
//                            .messageBody("{\"email\":\"priydevmishra389@gmail.com\", \"message\":\"Testing SQS\"}")
//                            .build()
//            );
//
//            System.out.println("MESSAGE SENT: " + resp.messageId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
