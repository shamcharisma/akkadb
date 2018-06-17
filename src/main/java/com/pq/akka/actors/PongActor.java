package com.pq.akka.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class PongActor extends AbstractActor {
    public static Props props(String response) {
        return Props.create(PongActor.class, response);
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
