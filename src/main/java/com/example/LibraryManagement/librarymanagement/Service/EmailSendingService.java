package com.example.LibraryManagement.librarymanagement.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailSendingService {

    @Autowired
    private SesClient sesClient;

    public void sendEmail(String to, String subject, String body){

        Destination destination = Destination.builder()
                .toAddresses(to).build();

        Content sub = Content.builder()
                .data(subject)
                .build();

        Content bodyContent = Content.builder()
                .data(body).build();

        Body bodyObj = Body.builder()
                .text(bodyContent).build();

        Message message =Message.builder()
                .subject(sub)
                .body(bodyObj)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(destination)
                .message(message)
                .source("priydevmishra389@gmail.com")
                .build();

        sesClient.sendEmail(request);
    }
}
