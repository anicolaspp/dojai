package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;

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
    public ParserQueryResult getQueryFrom(Statement statement) {
        
        return ParserQueryResult
                .builder()
                .type(ParserType.UNKNOWN)
                .build();
    }
}
