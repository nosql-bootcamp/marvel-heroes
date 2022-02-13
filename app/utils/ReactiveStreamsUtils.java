package utils;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReactiveStreamsUtils {

    public static <T> CompletableFuture<T> fromSinglePublisher(Publisher<T> publisher) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        publisher.subscribe(new Subscriber<T>() {

            private T value;

            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(T value) {
                this.value = value;
            }

            @Override
            public void onError(Throwable t) {
                future.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                future.complete(this.value);
            }
        });

        return future;
    }

    public static <T> CompletableFuture<List<T>> fromMultiPublisher(Publisher<T> publisher) {
        final CompletableFuture<List<T>> future = new CompletableFuture<>();
        publisher.subscribe(new Subscriber<T>() {

            private List<T> values = new ArrayList<>();

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Integer.MAX_VALUE);
            }

            @Override
            public void onNext(T value) {
                this.values.add(value);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                future.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                future.complete(this.values);
            }
        });

        return future;
    }

}
