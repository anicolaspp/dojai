package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.ojai.store.Connection;
import org.ojai.store.Query;

import java.util.ArrayList;
import java.util.List;

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
        
        val schema = addSelect(plainSelectBody, query);
        addWhere(plainSelectBody, query);
        addLimit(plainSelectBody, query);
        
        String table = getTableName(select);
        
        return ParserQueryResult
                .builder()
                .type(ParserType.SELECT)
                .query(query.build())
                .table(table)
                .selectFields(schema)
                .successful(true)
                .build();
    }
    
    private void addLimit(PlainSelect plainSelectBody, Query query) {
        val limit = plainSelectBody.getLimit().getRowCount();
        
        if (limit instanceof LongValue) {
            query.limit(((LongValue) limit).getValue());
        }
    }
    
    private String getTableName(Statement statement) {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder.getTableList(statement).get(0);
    }
    
    private List<String> addSelect(PlainSelect plainSelectBody, Query query) {
        List<String> fields = new ArrayList<>();
        
        plainSelectBody
                .getSelectItems()
                .forEach(selectItem -> {
                    
                    if (selectItem instanceof SelectExpressionItem) {
                        String columnName = ((Column) ((SelectExpressionItem) selectItem).getExpression()).getColumnName();
                        
                        query.select(columnName);
                        fields.add(columnName);
                    }
                });
        
        return fields;
    }
    
    private void addWhere(PlainSelect plainSelectBody, Query query) {
        val queryCondition = new WhereParser(connection).parse(plainSelectBody.getWhere());
        
        query.where(queryCondition);
    }
}