package com.github.anicolaspp.parsers.select;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.QueryFunctions;
import com.github.anicolaspp.parsers.update.UpdateStatementParser;
import lombok.val;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import org.ojai.store.Connection;
import org.ojai.store.Query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        val schema = getSchemaFrom(plainSelectBody);

        String table = QueryFunctions.getTableName(select);

        val query = QueryFunctions
                .addLimit(plainSelectBody.getLimit(), getInitialQuery(plainSelectBody, schema))
                .build();

        return ParserQueryResult
                .builder()
                .type(ParserType.SELECT)
                .query(query)
                .table(table)
                .selectFields(schema)
                .successful(true)
                .build();
    }

    private Query getInitialQuery(PlainSelect plainSelectBody, List<SelectField> schema) {
        return connection
                .newQuery()
                .select(schema.stream().map(SelectField::getName).toArray(String[]::new))
                .where(QueryFunctions.getQueryConditionFrom(plainSelectBody.getWhere(), connection, schema));
    }

    private List<SelectField> getSchemaFrom(PlainSelect plainSelectBody) {
        return plainSelectBody
                .getSelectItems()
                .stream()
                .map(selectItem -> {
                    if (selectItem instanceof SelectExpressionItem) {
                        String columnName = ((Column) ((SelectExpressionItem) selectItem).getExpression()).getColumnName();
                        String alias = getAliasName(((SelectExpressionItem) selectItem).getAlias());

                        return new SelectField(columnName, alias);
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private String getAliasName(Alias alias) {
        if (alias == null) {
            return null;
        } else {
            return alias.getName();
        }
    }
}

