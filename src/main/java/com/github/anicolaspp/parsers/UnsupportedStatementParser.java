package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;
import org.ojai.store.Query;

public class UnsupportedStatementParser implements ChainParser {
    
    private Connection connection;
    
    public UnsupportedStatementParser(Connection connection) {
        
        this.connection = connection;
    }
    
    @Override
    public ChainParser next() {
        return null;
    }
    
    @Override
    public Query getQueryFrom(Statement statement) {
        return connection.newQuery().build();
    }
}
