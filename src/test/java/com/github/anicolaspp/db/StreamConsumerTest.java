package com.github.anicolaspp.db;

import lombok.val;
import org.junit.Test;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamConsumerTest {

    @Test
    public void testConsumeEmptyStream() {
        val consumer = new StreamConsumer<Integer>(i -> { });

        assert consumer.runOn(Stream.empty()) == 0;
    }

    @Test
    public void testConsumeStreamItems() {
        StringBuilder builder = new StringBuilder();

        Consumer<Integer> fn = i -> builder.append(1);

        val consumer = new StreamConsumer<>(fn);

        int streamSize = new Random().nextInt(1000);

        assert consumer.runOn(IntStream.range(0, streamSize).boxed()) == streamSize;

        String builderValue = builder.toString();

        assert builderValue.length() == streamSize;

        assert builderValue.chars().allMatch(c -> ((char)c) == '1');
    }
}
