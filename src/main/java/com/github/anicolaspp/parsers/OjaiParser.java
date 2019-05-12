package com.github.anicolaspp.parsers;

import org.ojai.store.Query;

public interface OjaiParser {
    
    Query getQueryFrom(net.sf.jsqlparser.statement.Statement statement);
    
}