package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.ojai.store.Connection;
import org.ojai.store.Query;

import java.util.List;

public class QueryFunctions {

    private QueryFunctions() {
    }

    public static String getTableName(Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        return tablesNamesFinder.getTableList(statement).get(0);
    }

    public static void addWhere(Expression where, Query query, Connection connection, List<SelectField> schema) {
        val queryCondition = new WhereParser(connection, schema).parse(where);

        query.where(queryCondition);
    }

    public static void addLimit(Limit limit, Query query) {
        if (limit == null) {
            return;
        }

        val theLimit = limit.getRowCount();

        if (theLimit instanceof LongValue) {
            query.limit(((LongValue) theLimit).getValue());
        }
    }

    public static String getTableNameFrom(String systemTableName) {
        return systemTableName.replace("`","");
    }
}
