package com.github.anicolaspp.sql.connections;

public class InMemoryConnection implements DirectConnection {

    private static final String URL = "ojai:anicolaspp:mem";

    @Override
    public String getUrl() {
        return URL;
    }
}
