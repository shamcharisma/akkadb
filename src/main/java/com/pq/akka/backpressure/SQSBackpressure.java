package com.pq.akka.backpressure;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.alpakka.sqs.AttributeName;
import akka.stream.alpakka.sqs.MessageAttributeName;
import akka.stream.alpakka.sqs.SqsSourceSettings;
import akka.stream.alpakka.sqs.javadsl.SqsSink;
import akka.stream.alpakka.sqs.javadsl.SqsSource;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import scala.collection.immutable.Seq;

import java.time.Instant;
import java.util.concurrent.CompletionStage;

public class SQSBackpressure {
    ActorSystem system = ActorSystem.create();
    ActorMaterializer materializer = ActorMaterializer.create(system);
    String sqsEndpoint = "https://sqs.us-east-1.amazonaws.com";
    String queueUrl = "https://sqs.us-east-1.amazonaws.com/681160064311/AkkaStream-Test-SQS";
    AmazonSQSAsync awsSqsClient;

    public void getCredentials() {
        AWSCredentialsProvider credentialsProvider =
                new AWSStaticCredentialsProvider(new BasicAWSCredentials("AKIAJMKCM3DWKTO5PLAA", "vIk5HzaZaA3fBBlDkqi/wbIysCN17uvX0HkED1ZK"));
        awsSqsClient = AmazonSQSAsyncClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(sqsEndpoint, "us-east-1"))
                .build();
        system.registerOnTermination(() -> awsSqsClient.shutdown());
    }

    public void writeToSQSQueue() {
        CompletionStage<Done> done = Source
                .single(new SendMessageRequest().withMessageBody("alpakka"))
                .runWith(SqsSink.messageSink(queueUrl, awsSqsClient), materializer);
    }

    public void readFromSQSQueue() {
      /*  final CompletionStage<String> cs = SqsSource.create(queueUrl, new SqsSourceSettings(20, 100, 10, new Seq()), awsSqsClient)
                .map(Message::getBody)
                .take(1)
                .runWith(Sink.head(), materializer);*/
    }

   /* private void setSQSSourceSettings() {
                SqsSourceSettings sqsSourceSettings = SqsSourceSettings.create(20, 100, 10,
                        new Seq<AttributeName>("tst")
                        Seq<MessageAttributeName> messageAttributeNames,
                        boolean closeOnEmptyReceive);

                sqsSourceSettings
                        .withWaitTimeSeconds(20)
                        .withMaxBufferSize(100)
                        .withMaxBatchSize(10)
                        .withAttributes("sham-13", Instant.now())
                        .withMessageAttributes(MessageAttributeName.apply("bar.*"))
                        .withCloseOnEmptyReceive();
    }*/

}
