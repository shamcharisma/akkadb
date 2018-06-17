package com.pq.akka.backpressure;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.pq.akka.backpressure.Utils.sleep;

public class TempReactiveStream {
    public void generateStream() {
        Flowable<Long> naturalNumbers =
                Flowable.generate(() -> 0L, (state, emitter) -> {
                    emitter.onNext(state);
                    return state + 1;
                });
    }

    public static boolean overflowWithSingleThreadedUnboundedQueue() {
        System.setProperty("rx.ring-buffer.size", "2");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Scheduler scheduler = Schedulers.from(executorService);

        PublishSubject<Integer> subject = PublishSubject.create();
        subject.observeOn(scheduler)
                .subscribe(System.out::println);

        // onNext 12 times into the subject...
        for (int i = 1; i <= 20; i++) {
            subject.onNext(i);
        }
        return subject.hasComplete();
    }

    public static void overflowWithRxJava() {
        Flowable.range(1, 1_000_000)
                .observeOn(Schedulers.computation())
                .subscribe(System.out::println);

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void backpressure() {
        SomeFeed<PriceTick> feed = new SomeFeed<>();
        Flowable<PriceTick> flowable = Flowable.create(emitter ->
        {
            SomeListener listener = new SomeListener() {
                @Override
                public void priceTick(PriceTick event) {
                    emitter.onNext(event);
                    if (event.isLast()) {
                        emitter.onComplete();
                    }
                }

                @Override
                public void error(Throwable e) {
                    emitter.onError(e);
                }
            };
            feed.register(listener);
        }, BackpressureStrategy.BUFFER);

        ConnectableFlowable<PriceTick> hotObservable = flowable.publish();
        hotObservable.connect();

        hotObservable.take(10_000).subscribe((priceTick) ->
                System.out.printf("1 %s %4s %6.2f%n", priceTick.getDate(),
                        priceTick.getInstrument(), priceTick.getPrice()));

        sleep(1_000);

        hotObservable.take(10).subscribe((priceTick) ->
                System.out.printf("2 %s %4s %6.2f%n", priceTick.getDate(),
                        priceTick.getInstrument(), priceTick.getPrice()));
    }


    private static int compute(int x) {
        return x^2;
    }
}
