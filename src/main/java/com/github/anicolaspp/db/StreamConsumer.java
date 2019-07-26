package com.github.anicolaspp.db;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

class StreamConsumer<A> {
    private Consumer<A> consumer;

    public StreamConsumer(Consumer<A> consumer) {
        this.consumer = consumer;
    }

    /**
     * Execute the given consumer on the stream
     *
     * @param stream Stream to run Consumer on
     * @return Number of values consumed
     */
    int runOn(Stream<A> stream) {
        AtomicInteger count = new AtomicInteger();

        stream.forEach(value -> {
            consumer.accept(value);

            count.addAndGet(1);
        });

        return count.get();
    }
}
