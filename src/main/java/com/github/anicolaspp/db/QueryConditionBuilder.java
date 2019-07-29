package com.github.anicolaspp.db;

import com.github.anicolaspp.parsers.select.SelectField;
import net.sf.jsqlparser.expression.Expression;
import org.ojai.store.Connection;
import org.ojai.store.QueryCondition;

import java.util.List;

public class QueryConditionBuilder {

    private Expression where;

    public QueryConditionBuilder(Expression where) {

        this.where = where;
    }

    public static QueryConditionBuilder from(Expression where) {
        return new QueryConditionBuilder(where);
    }

    public QueryCondition buildWith(Connection connection, List<SelectField> schema) {
        return new WhereParser(connection, schema).parse(where);
    }
}
