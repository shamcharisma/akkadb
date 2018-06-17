package com.pq.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.lightbend.akka.sample.Greeter;
import com.lightbend.akka.sample.Greeter.*;
import com.lightbend.akka.sample.Printer;
import com.pq.akka.actors.AkkaDB;
import com.pq.akka.backpressure.SQSJavaClient;
import com.pq.akka.messages.SetRequest;

import java.io.IOException;
import java.util.stream.IntStream;

public class AkkaStart {
   /* public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("akkadb");
        try {
            //#create-actors
            final ActorRef akkadb = system.actorOf(AkkaDB.props(), "akkadb");

            //#main-send-messages
            akkadb.tell(new SetRequest("this is a new key", "this is a new value"), ActorRef.noSender());

            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException e) {
            System.out.println("Error sending message to the actor: " + e);
        } finally {
            system.terminate();
        }
    }*/

    public static void main(String[] args) {
        final String queueName = "AkkaStream-Test-SQS";

        try {
            //Create SQS
            final AmazonSQSAsync amazonSQS = SQSJavaClient.createQueue(queueName);
            final SQSJavaClient sqsJavaClient = new SQSJavaClient(amazonSQS, queueName);

            //Send message
            //IntStream.range(0, 10_000).forEach(x -> sqsJavaClient.sendMessage());

            //Receive message
            sqsJavaClient.receieveMessages();

        } catch (final AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means " +
                    "your request made it to Amazon SQS, but was " +
                    "rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (final AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means " +
                    "the client encountered a serious internal problem while " +
                    "trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
