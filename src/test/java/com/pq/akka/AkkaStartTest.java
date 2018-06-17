package com.pq.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import akka.testkit.javadsl.TestKit;
import com.pq.akka.actors.AkkaDB;
import com.pq.akka.messages.SetRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AkkaStartTest {
    static ActorSystem system = ActorSystem.create();

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testAkkaDbSetRequestSuccessfully() {
        final TestActorRef<AkkaDB> testActorRef = TestActorRef.create(system, Props.create(AkkaDB.class));

        testActorRef.tell(new SetRequest("testKey", "testValue"), ActorRef.noSender());
        AkkaDB akkadb = testActorRef.underlyingActor();
        assertEquals("testValue", akkadb.map.get("testKey"));
    }
}
