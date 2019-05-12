package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;
import org.ojai.store.Query;

public class InsertStatementParser implements ChainParser {
    
    private Connection connection;
    
    public InsertStatementParser(Connection connection) {
        
        this.connection = connection;
    }
    
    @Override
    public ChainParser next() {
        return new UnsupportedStatementParser(connection);
    }
    
    @Override
    public Query getQueryFrom(Statement statement) {
        return connection.newQuery().build();
    }
}
