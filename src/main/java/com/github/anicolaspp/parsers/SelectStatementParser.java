package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import org.ojai.store.Connection;
import org.ojai.store.Query;

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
    public Query getQueryFrom(Statement statement) {
        
        //TODO: try parsing a select. If it fails, calls next.getQueryFrom
        
        if (!(statement instanceof Select)) {
            return emptyQuery(connection);
        }
        
        val select = (Select) statement;
        
        if (!(select.getSelectBody() instanceof PlainSelect)) {
            return emptyQuery(connection);
        }
        
        val plainSelectBody = (PlainSelect) select.getSelectBody();
        
        val query = connection.newQuery();
        
        plainSelectBody.getSelectItems().forEach(selectItem -> {
    
            if (selectItem instanceof SelectExpressionItem) {
                String columnName = ((Column) ((SelectExpressionItem) selectItem).getExpression()).getColumnName();
                
                query.select(columnName);
            }
        });
        
        return query.build();
    }
}
