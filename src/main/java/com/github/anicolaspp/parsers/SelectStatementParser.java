package com.github.anicolaspp.parsers;

import lombok.val;
import net.sf.jsqlparser.expression.Alias;
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
        QueryFunctions.addWhere(plainSelectBody.getWhere(), query, connection, schema);
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

    private List<SelectField> addSelect(PlainSelect plainSelectBody, Query query) {
        List<SelectField> fields = new ArrayList<>();

        plainSelectBody
                .getSelectItems()
                .forEach(selectItem -> {

                    if (selectItem instanceof SelectExpressionItem) {
                        String columnName = ((Column) ((SelectExpressionItem) selectItem).getExpression()).getColumnName();
                        String alias = getAliasName(((SelectExpressionItem) selectItem).getAlias()); //.getAlias().getName();

                        query.select(columnName);
                        fields.add(new SelectField(columnName, alias));
                    }
                });

        return fields;
    }

    private String getAliasName(Alias alias) {
        if (alias == null) {
            return null;
        } else {
            return alias.getName();
        }
    }
}

