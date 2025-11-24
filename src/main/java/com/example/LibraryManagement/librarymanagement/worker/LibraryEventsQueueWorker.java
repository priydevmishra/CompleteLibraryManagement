package com.example.LibraryManagement.librarymanagement.worker;

import com.example.LibraryManagement.librarymanagement.Service.EmailSendingService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class LibraryEventsQueueWorker {

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private EmailSendingService emailSendingService;

    @Value("${aws.sqs.issue-return-notification-queue-url}")
    private String issueReturnQueueURL;

    private final ObjectMapper mapper = new ObjectMapper();

    //fixedDelay use kiyaa, to avoid overlapping runs when using long polling
    @Scheduled(fixedDelay = 5000)
    public void pollQueue() {
        ReceiveMessageRequest req = ReceiveMessageRequest.builder()
                .queueUrl(issueReturnQueueURL)
                .waitTimeSeconds(20)            // long poll
                .maxNumberOfMessages(5)
                .messageAttributeNames("All")
                .build();

        ReceiveMessageResponse response;
        try {
            response = sqsClient.receiveMessage(req);
        } catch (Exception ex) {
            log.error("Failed to receive messages from SQS", ex);
            return;
        }

        if (response == null || response.messages().isEmpty()) {
            return;
        }

        List<Message> messages = response.messages();
        for (Message msg : messages) {
            // handle each message independently
            try {
                boolean ok = processMessage(msg);
                if (ok) {
                    deleteMessage(msg);
                } else {
                    // If not ok, do not delete. Let SQS re-deliver after visibility timeout.
                    log.warn("Processing returned false, leaving message for retry. messageId={}", msg.messageId());
                }
            } catch (Exception e) {
                // catch-all to avoid breaking the loop
                log.error("Unhandled error processing messageId={}", msg.messageId(), e);
                // do not delete; message will be retried / eventually land in DLQ (if configured)
            }
        }
    }

    // Return true if processing was successful and message can be deleted
    private boolean processMessage(Message msg) {
        Map data;
        try {
            data = mapper.readValue(msg.body(),
                    Map.class);
        } catch (Exception e) {
            log.error("Failed to parse message body as JSON. messageId={}, body={}", msg.messageId(), msg.body(), e);
            // malformed message -> consider deleting or send to DLQ manually
            return false;
        }

        String eventType = Optional.ofNullable(data.get("eventType")).map(Object::toString).orElse(null);
        if (eventType == null) {
            log.warn("Missing eventType in message. messageId={}, body={}", msg.messageId(), msg.body());
            return false;
        }

        String to = Optional.ofNullable(data.get("email")).map(Object::toString).orElse(null);
        String body = Optional.ofNullable(data.get("body")).map(Object::toString).orElse("");

        // email is mandatory for your flow
        if (to == null) {
            log.warn("Missing email in message for event {}. messageId={}", eventType, msg.messageId());
            return false;
        }

        try {
            switch (eventType) {
                case "ISSUE_BOOK":
                    emailSendingService.sendEmail(to, "Book Issued Successfully", body);
                    break;
                case "RETURN_BOOK":
                    emailSendingService.sendEmail(to, "Book Returned Successfully", body);
                    break;
                case "DUE_REMINDER":
                    // implement reminder behaviour — e.g. different subject/template
                    emailSendingService.sendEmail(to, "Book Due Tomorrow", body);
                    break;
                default:
                    log.warn("Unhandled EventType={} messageId={}", eventType, msg.messageId());
                    // If you want to delete unknown events, return true. Otherwise false.
                    return false;
            }
            // If sendEmail throws, catch below. If returns normally, success.
            log.info("Processed eventType={} messageId={} to={}", eventType, msg.messageId(), to);
            return true;
        } catch (Exception e) {
            // email sending failed — do not delete message so it can be retried
            log.error("Failed to process messageId={} eventType={} to={}", msg.messageId(), eventType, to, e);
            return false;
        }
    }

    private void deleteMessage(Message message) {
        try {
            DeleteMessageRequest deleteReq = DeleteMessageRequest.builder()
                    .queueUrl(issueReturnQueueURL)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteReq);
            log.info("Deleted messageId={}", message.messageId());
        } catch (Exception e) {
            log.error("Failed to delete messageId={}", message.messageId(), e);
            // If deletion fails, the message will reappear — handle with care
        }
    }
}
