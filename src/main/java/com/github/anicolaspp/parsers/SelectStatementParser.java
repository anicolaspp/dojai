package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.ojai.store.Connection;
import org.ojai.store.Query;
import org.ojai.store.QueryCondition;

public class SelectStatementParser implements ChainParser {
    
    private Connection connection;
    
    public SelectStatementParser(Connection connection) {
        
        this.connection = connection;
    }
    
    @Override
    public ChainParser next() {
        return new InsertStatementParser(connection);
    }
    
    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {
        
        if (!(statement instanceof Select)) {
            return ParserQueryResult
                    .builder()
                    .query(emptyQuery(connection))
                    .build();
        }
        
        return parseSelect((Select) statement);
    }
    
    private ParserQueryResult parseSelect(Select select) {
        
        if (!(select.getSelectBody() instanceof PlainSelect)) {
            return ParserQueryResult
                    .builder()
                    .query(emptyQuery(connection))
                    .build();
        }
        
        val plainSelectBody = (PlainSelect) select.getSelectBody();
        
        val query = connection.newQuery();
        
        addSelect(plainSelectBody, query);
        
        addWhere(plainSelectBody, query);
        
        String table = getTableName(select);
        
        return ParserQueryResult
                .builder()
                .type(ParserType.SELECT)
                .query(query.build())
                .table(table)
                .successful(true)
                .build();
    }
    
    private String getTableName(Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder.getTableList(statement).get(0);
    }
    
    private void addSelect(PlainSelect plainSelectBody, Query query) {
        plainSelectBody
                .getSelectItems()
                .forEach(selectItem -> {
                    
                    if (selectItem instanceof SelectExpressionItem) {
                        String columnName = ((Column) ((SelectExpressionItem) selectItem).getExpression()).getColumnName();
                        
                        query.select(columnName);
                    }
                });
    }
    
    private void addWhere(PlainSelect plainSelectBody, Query query) {
        val queryCondition = new WhereParser(connection).parse(plainSelectBody.getWhere());
        
        query.where(queryCondition);
    }
}

class WhereParser {
    
    private Connection connection;
    
    WhereParser(Connection connection) {
        
        this.connection = connection;
    }
    
    QueryCondition parse(Expression where) {
        
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
    
    private QueryCondition cmp(String field, String value, QueryCondition.Op op) {
        return connection
                .newCondition()
                .is(field, op, value)
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
