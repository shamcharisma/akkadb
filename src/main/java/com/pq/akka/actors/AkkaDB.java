package com.pq.akka.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.google.common.annotations.VisibleForTesting;
import com.pq.akka.messages.SetRequest;

import java.util.HashMap;
import java.util.Map;

public class AkkaDB extends AbstractActor {
    public final Map<String, Object> map = new HashMap<>();

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    static public Props props() {
        return Props.create(AkkaDB.class, AkkaDB::new);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(SetRequest.class, this::logAndSetMap)
                .matchAny(o -> log.info("Received unknown message {}", o))
                .build();
    }

    @VisibleForTesting
    void logAndSetMap(SetRequest message) {
        log.info("Received request: key - {}, value - {}",
                message.getKey(), message.getValue());
        map.put(message.getKey(), message.getValue());
    }

}
