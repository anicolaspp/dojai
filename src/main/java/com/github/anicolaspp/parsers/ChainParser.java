package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;
import org.ojai.store.Query;

public interface ChainParser extends OjaiParser {
    
    OjaiParser next();
    
    @Override
    Query getQueryFrom(Statement statement);
    
    
    static ChainParser build(Connection connection) {
        return new SelectStatementParser(connection);
    }
}
