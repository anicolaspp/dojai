package com.github.anicolaspp.parsers;

import org.ojai.store.Connection;
import org.ojai.store.Query;

public interface OjaiParser {
    
    /**
     * Responsible for parsing the statement
     *
     * @param statement Statement to be parsed
     * @return returns Query or emptyQuery query
     */
    ParserQueryResult getQueryFrom(net.sf.jsqlparser.statement.Statement statement);
    
    /**
     * Represents an empty query. Can be used by implementors for shorthand creation of empty ojai query.
     *
     * @param connection connection used to create the empty query
     * @return an empty query `{}`
     */
    default Query emptyQuery(Connection connection) {
        return connection.newQuery().build();
    }
}

