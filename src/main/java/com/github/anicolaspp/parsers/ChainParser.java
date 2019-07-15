package com.github.anicolaspp.parsers;

import com.github.anicolaspp.parsers.select.SelectStatementParser;
import lombok.val;
import net.sf.jsqlparser.statement.Statement;
import org.ojai.store.Connection;

public interface ChainParser extends OjaiParser {

    ChainParser next();

    default ParserQueryResult parse(Statement statement) throws Exception {
        val result = getQueryFrom(statement);

        if (result.getType() == ParserType.UNKNOWN) {
            throw new Exception("Parsing Exception");
        }

        if (!result.getSuccessful()) {
            return next().parse(statement);
        } else {
            return result;
        }
    }

    static ChainParser build(Connection connection) {
        return new SelectStatementParser(connection);
    }
}
