package com.github.anicolaspp.parsers;

import com.github.anicolaspp.parsers.select.SelectField;
import com.mapr.ojai.store.impl.Values;
import javafx.util.Pair;
import lombok.val;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.ojai.Document;
import org.ojai.Value;
import org.ojai.store.Connection;
import org.ojai.store.Query;
import org.ojai.store.QueryCondition;
import org.ojai.types.ODate;
import org.ojai.types.OTimestamp;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryFunctions {

    private QueryFunctions() {
    }

    public static <A> Map<String, Object> project(List<A> columns,
                                                  Document document,
                                                  Function<A, String> columnNameExtractor) {
        return columns
                .stream()
                .map(column -> {
                    val columnName = columnNameExtractor.apply(column);

                    val value = document.getValue(columnName);

                    return new Pair<>(columnName, value);
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public static String getTableName(Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        return tablesNamesFinder.getTableList(statement).get(0).replace("`", "");
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

    public static Value valueFromExpression(Expression expression) {
        if (expression instanceof Column) {
            return new Values.StringValue(((Column) expression).getColumnName());
        }

        if (expression instanceof LongValue) {
            return new Values.LongValue(((LongValue) expression).getValue());
        }

        if (expression instanceof DoubleValue) {
            return new Values.DoubleValue(((DoubleValue) expression).getValue());
        }

        if (expression instanceof DateValue) {
            return new Values.DateValue(new ODate(((DateValue) expression).getValue()));
        }

        if (expression instanceof TimestampValue) {
            return new Values.TimestampValue(new OTimestamp(((TimestampValue) expression).getValue().getTime()));
        }

        return Values.NULL;
    }
}
