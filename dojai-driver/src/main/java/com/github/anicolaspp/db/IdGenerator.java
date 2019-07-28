package com.github.anicolaspp.db;

import java.util.UUID;

public class IdGenerator {
    private static IdGenerator ID_GENERATOR;

    private IdGenerator() {
    }

    public static IdGenerator getInstance() {
        if (ID_GENERATOR == null) {
            ID_GENERATOR = new IdGenerator();
        }

        return ID_GENERATOR;
    }

    public String nextId() {
        return new scala.util.Random().nextString(3) + UUID.randomUUID().toString();
    }
}
