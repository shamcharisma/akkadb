package com.pq.akka.backpressure;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.Map;

public class SQSJavaClient {
    private final String queueUrl;
    private final String queueName;
    private final AmazonSQSAsync amazonSQS;
    private static final String sqsRegion = "us-east-1";
    private static final String sqsEndpoint = "https://sqs.us-east-1.amazonaws.com";


    public SQSJavaClient(AmazonSQSAsync amazonSQS, String queueName) {
        this.amazonSQS = amazonSQS;
        this.queueName = queueName;
        this.queueUrl = amazonSQS.getQueueUrl(queueName).getQueueUrl();
    }

    //todo: add synchronize
    public static AmazonSQSAsync createQueue(String queueName) {
        System.out.println(String.format("Creating a new SQS queue called %s: ", queueName));
        final AmazonSQSAsync sqs = buildAmazonSQS();

        final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        sqs.createQueue(createQueueRequest);

        return sqs;
    }

    public static AmazonSQSAsync buildAmazonSQS() {
        final AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAJOBODO6BT566TDNA", "K+8aoElD9xtzv0GMsY4kD9mvRrC5GFipH2UeqINk"));
        return AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(sqsEndpoint, sqsRegion))
                .build();
    }

    public void sendMessage() {
        amazonSQS.sendMessage(new SendMessageRequest(queueUrl, String.format("This is a Test: %s", Math.random())));
    }

    public void receieveMessages() {
        System.out.println(String.format("Receiving messages from queue %s: ", queueName));
        final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl).withMaxNumberOfMessages(10);
        final List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
        System.out.println(String.format("Number of messages in queue %d", messages.size()));

        for (final Message message : messages) {
            System.out.println("MessageId: " + message.getMessageId());
            for (final Map.Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("Attr Name: " + entry.getKey());
                System.out.println("Attr Value: " + entry.getValue());
            }
            //Delete message after receipt
            deleteMessagesAfterReceipt(message);
        }
        System.out.println("Messages deleted after receipt acknowledged");
    }

    private void deleteMessagesAfterReceipt(Message message) {
        final String messageReceiptHandle = message.getReceiptHandle();
        amazonSQS.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(queueUrl)
                .withReceiptHandle(messageReceiptHandle));
    }
}
