package com.github.anicolaspp.db;

import com.mapr.ojai.store.impl.Values;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.schema.Column;
import org.ojai.Value;
import org.ojai.types.ODate;
import org.ojai.types.OTimestamp;

public class ValueExtractor {

    public static Value from(Expression expression) {
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

        if (expression instanceof StringValue) {
            return new Values.StringValue(((StringValue) expression).getValue());
        }

        return Values.NULL;
    }
}
