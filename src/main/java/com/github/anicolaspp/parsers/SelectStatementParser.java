package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;
import org.ojai.store.Query;

public class SelectStatementParser implements ChainParser {
    
    private Connection connection;
    
    public SelectStatementParser(Connection connection) {
        
        this.connection = connection;
    }
    
    @Override
    public ChainParser next() {
        return new InsertStatementParser(connection);
    }
    
    @Override
    public Query getQueryFrom(Statement statement) {
        
        //TODO: try parsing a select. If it fails, calls next.getQueryFrom
        
        
        
        return emptyQuery(connection);
    }
}
