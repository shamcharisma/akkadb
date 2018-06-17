package com.pq.akka.backpressure;

import io.reactivex.exceptions.MissingBackpressureException;
import org.junit.Test;

public class ReactiveStreamTest {
    @Test//(expected = MissingBackpressureException.class)
    public void using_single_thread_to_observe_overflow_in_queues() {
        ReactiveStream.backpressure();
    }
}
