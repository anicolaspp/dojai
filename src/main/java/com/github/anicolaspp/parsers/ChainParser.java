package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;
import org.ojai.store.Query;

public interface ChainParser extends OjaiParser {
    
    ChainParser next();
    
    default Query parse(Statement statement) {
        val query = getQueryFrom(statement);
        
        if (query.isEmpty()) {
            return next().parse(statement);
        } else {
            return query;
        }
    }
    
    static ChainParser build(Connection connection) {
        return new SelectStatementParser(connection);
    }
}
