package com.github.anicolaspp.parsers;

import org.ojai.store.Connection;

public interface ChainParser extends OjaiParser {
    
    OjaiParser next();
    
    static ChainParser build(Connection connection) {
        return new SelectStatementParser(connection);
    }
}
