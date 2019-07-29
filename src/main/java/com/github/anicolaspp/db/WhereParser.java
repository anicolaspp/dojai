package com.github.anicolaspp.db;

import com.github.anicolaspp.parsers.select.SelectField;
import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
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
                .filter(field -> field.getAlias() != null)
                .filter(field -> field.getAlias().equals(label))
                .findAny()
                .map(SelectField::getName)
                .orElse(label);
    }

    private QueryCondition cmp(String field, String value, QueryCondition.Op op) {
        return connection
                .newCondition()
                .is(getName(field), op, value.replace("'",""))
                .build();
    }

    private QueryCondition parseMinorThan(MinorThan minorThan) {
        return cmp(
                minorThan.getLeftExpression().toString(),
                minorThan.getRightExpression().toString(),
                QueryCondition.Op.LESS);
    }

    private QueryCondition parseMinorThanEquals(MinorThanEquals minorThanEquals) {
        return cmp(
                minorThanEquals.getLeftExpression().toString(),
                minorThanEquals.getRightExpression().toString(),
                QueryCondition.Op.LESS_OR_EQUAL);
    }

    private QueryCondition parseGreaterThan(GreaterThan greaterThan) {
        return cmp(
                greaterThan.getLeftExpression().toString(),
                greaterThan.getRightExpression().toString(),
                QueryCondition.Op.GREATER);
    }

    private QueryCondition parseGreaterThanEquals(GreaterThanEquals greaterThanEquals) {
        return cmp(
                greaterThanEquals.getLeftExpression().toString(),
                greaterThanEquals.getRightExpression().toString(),
                QueryCondition.Op.GREATER_OR_EQUAL);
    }

    private QueryCondition parseEqualsTo(EqualsTo equalsTo) {
        return cmp(
                equalsTo.getLeftExpression().toString(),
                equalsTo.getRightExpression().toString(),
                QueryCondition.Op.EQUAL);
    }

}
