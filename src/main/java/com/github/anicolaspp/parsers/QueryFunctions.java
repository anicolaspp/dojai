package com.github.anicolaspp.parsers;

import com.github.anicolaspp.parsers.select.SelectField;
import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.ojai.store.Connection;
import org.ojai.store.Query;
import org.ojai.store.QueryCondition;

import java.util.List;

public class QueryFunctions {

    private QueryFunctions() {
    }

    public static String getTableName(Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        return tablesNamesFinder.getTableList(statement).get(0).replace("`","");
    }

    public static QueryCondition getQueryConditionFrom(Expression where, Connection connection, List<SelectField> schema) {
        return new WhereParser(connection, schema).parse(where);
    }

    public static Query addLimit(Limit limit, Query query) {
        if (limit == null) {
            return query;
        }

        val theLimit = limit.getRowCount();

        if (theLimit instanceof LongValue) {
            query.limit(((LongValue) theLimit).getValue());

            return query;
        }

        return query;
    }
}
