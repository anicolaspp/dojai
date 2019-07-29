package com.github.anicolaspp.db;

import com.github.anicolaspp.parsers.select.SelectField;
import com.mapr.ojai.store.impl.Values;
import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import org.ojai.Value;
import org.ojai.store.Connection;
import org.ojai.store.QueryCondition;

import java.util.List;

public class WhereParser {

    private Connection connection;
    private List<SelectField> schema;

    public WhereParser(Connection connection, List<SelectField> schema) {
        this.connection = connection;
        this.schema = schema;
    }

    public QueryCondition parse(Expression where) {

        if (where instanceof EqualsTo) {
            return parseEqualsTo((EqualsTo) where);
        } else if (where instanceof GreaterThan) {
            return parseGreaterThan((GreaterThan) where);
        } else if (where instanceof GreaterThanEquals) {
            return parseGreaterThanEquals((GreaterThanEquals) where);
        } else if (where instanceof MinorThan) {
            return parseMinorThan((MinorThan) where);
        } else if (where instanceof MinorThanEquals) {
            return parseMinorThanEquals((MinorThanEquals) where);
        } else if (where instanceof AndExpression) {
            return parseAnd((AndExpression) where);
        } else if (where instanceof OrExpression) {
            return parseOr((OrExpression) where);
        }

        return connection.newCondition().build();
    }

    private QueryCondition parseOr(OrExpression or) {
        val left = parse(or.getLeftExpression());
        val right = parse(or.getRightExpression());

        return connection.newCondition()
                .or()
                .condition(left)
                .condition(right)
                .close()
                .build();
    }

    private QueryCondition parseAnd(AndExpression and) {

        val left = parse(and.getLeftExpression());
        val right = parse(and.getRightExpression());

        return connection.newCondition()
                .and()
                .condition(left)
                .condition(right)
                .close()
                .build();
    }

    private String getName(String label) {
        return schema
                .stream()
                .filter(field -> field.getValue().equals(label))
                .findAny()
                .map(SelectField::getName)
                .orElse(label);
    }

    private QueryCondition cmp(String field, Value value, QueryCondition.Op op) {

        if (value instanceof Values.FloatValue) {
            return connection.newCondition().is(getName(field), op, value.getFloat()).build();
        }

        if (value instanceof Values.StringValue) {
            return connection.newCondition().is(getName(field), op, value.getString()).build();
        }
        if (value instanceof Values.LongValue) {
            return connection.newCondition().is(getName(field), op, value.getLong()).build();
        }

        if (value instanceof Values.IntValue) {
            return connection.newCondition().is(getName(field), op, value.getInt()).build();
        }

        if (value instanceof Values.DoubleValue) {
            return connection.newCondition().is(getName(field), op, value.getDouble()).build();
        }

        if (value instanceof Values.DateValue) {
            return connection.newCondition().is(getName(field), op, value.getDate()).build();
        }

        if (value instanceof Values.TimestampValue) {
            return connection.newCondition().is(getName(field), op, value.getTimestamp()).build();
        }

        if (value instanceof Values.BooleanValue) {
            return connection.newCondition().is(getName(field), op, value.getBoolean()).build();
        }

        if (value instanceof Values.DecimalValue) {
            return connection.newCondition().is(getName(field), op, value.getDecimal()).build();
        }

        if (value instanceof Values.ShortValue) {
            return connection.newCondition().is(getName(field), op, value.getShort()).build();
        }

        if (value instanceof Values.TimeValue) {
            return connection.newCondition().is(getName(field), op, value.getTime()).build();
        }

        return connection.newCondition().build();
    }

    private QueryCondition parseMinorThan(MinorThan minorThan) {
        return cmp(
                minorThan.getLeftExpression().toString(),
                ValueExtractor.from(minorThan.getRightExpression()),
                QueryCondition.Op.LESS);
    }

    private QueryCondition parseMinorThanEquals(MinorThanEquals minorThanEquals) {
        return cmp(
                minorThanEquals.getLeftExpression().toString(),
                ValueExtractor.from(minorThanEquals.getRightExpression()),
                QueryCondition.Op.LESS_OR_EQUAL);
    }

    private QueryCondition parseGreaterThan(GreaterThan greaterThan) {
        return cmp(
                greaterThan.getLeftExpression().toString(),
                ValueExtractor.from(greaterThan.getRightExpression()),
                QueryCondition.Op.GREATER);
    }

    private QueryCondition parseGreaterThanEquals(GreaterThanEquals greaterThanEquals) {
        return cmp(
                greaterThanEquals.getLeftExpression().toString(),
                ValueExtractor.from(greaterThanEquals.getRightExpression()),
                QueryCondition.Op.GREATER_OR_EQUAL);
    }

    private QueryCondition parseEqualsTo(EqualsTo equalsTo) {
        return cmp(
                equalsTo.getLeftExpression().toString(),
                ValueExtractor.from(equalsTo.getRightExpression()),
                QueryCondition.Op.EQUAL);
    }
}
