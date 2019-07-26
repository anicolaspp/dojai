package com.github.anicolaspp.db.connections;

public class MapRDBConnection implements DirectConnection {

    private static final  String URL = "ojai:mapr:";

    @Override
    public String getUrl() {
        return URL;
    }
}
