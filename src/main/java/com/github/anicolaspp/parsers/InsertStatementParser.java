package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;

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
    public ParserQueryResult getQueryFrom(Statement statement) {
        return ParserQueryResult
                .builder()
                .successful(false)
                .build();
    }
}
