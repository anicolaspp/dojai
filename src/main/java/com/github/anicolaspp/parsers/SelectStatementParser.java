package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
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
        return new UpdateStatementParser(connection);
    }
    
    @Override
    public ParserQueryResult getQueryFrom(Statement statement) {
        
        if (!(statement instanceof Select)) {
            return ParserQueryResult
                    .builder()
                    .successful(false)
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
        QueryFunctions.addWhere(plainSelectBody.getWhere(), query, connection);
        QueryFunctions.addLimit(plainSelectBody.getLimit(), query);
        
        String table = QueryFunctions.getTableName(select);
        
        return ParserQueryResult
                .builder()
                .type(ParserType.SELECT)
                .query(query.build())
                .table(table)
                .selectFields(schema)
                .successful(true)
                .build();
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
}

