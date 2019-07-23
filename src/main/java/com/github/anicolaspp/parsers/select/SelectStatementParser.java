package com.github.anicolaspp.parsers.select;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.QueryFunctions;
import com.github.anicolaspp.parsers.StoreManager;
import com.github.anicolaspp.parsers.update.UpdateStatementParser;
import javafx.util.Pair;
import lombok.val;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.ojai.Document;
import org.ojai.store.Connection;
import org.ojai.store.Query;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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

        return runSelect((PlainSelect) select.getSelectBody());
    }

    private ParserQueryResult runSelect(PlainSelect plainSelectBody) {
        val from = plainSelectBody.getFromItem();

        if (from instanceof SubSelect) {

            val result = runSubSelect((SubSelect) from);

            Stream<Document> projectedDocs = result
                    .getDocuments()
                    .map(document -> getMapDoc(result, document))
                    .map(connection::newDocument);

            return ParserQueryResult
                    .<Document>builder()
                    .type(ParserType.SELECT)
                    .query(result.getQuery())
                    .table(getTable(from))
                    .selectFields(getSchemaFrom(plainSelectBody))
                    .successful(true)
                    .documents(projectedDocs)
                    .build();

        } else {
            val schema = getSchemaFrom(plainSelectBody);

            val query = QueryFunctions
                    .addLimit(plainSelectBody.getLimit(), getInitialQuery(plainSelectBody, schema))
                    .build();

            val store = StoreManager.getStoreFor(getTable(from), connection);

            val documents = StreamSupport.stream(store.find(query).spliterator(), false);

            return ParserQueryResult.<Document>builder()
                    .query(query)
                    .table(getTable(from))
                    .selectFields(schema)
                    .successful(true)
                    .type(ParserType.SELECT)
                    .documents(documents)
                    .build();
        }
    }

    private Map<String, Object> getMapDoc(ParserQueryResult<Document> result, Document document) {
        return result
                .getSelectFields()
                .stream()
                .map(selectField -> {
                    val value = document.getValue(selectField.getName());

                    return new Pair<>(
                            selectField.getAlias() == null ? selectField.getName() : selectField.getAlias(),
                            value);

                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private ParserQueryResult<Document> runSubSelect(SubSelect from) {

        val query = runSelect((PlainSelect) from.getSelectBody());

        val store = connection.getStore(query.getTable());

        val documents = StreamSupport.stream(store.find(query.getQuery()).spliterator(), false);

        return new ParserQueryResult<>(query.getQuery(), query.getTable(), query.getSelectFields(), true, ParserType.SELECT, documents);
    }

    private String getTable(FromItem from) {
        if (from instanceof Table) {
            return ((Table) from).getName().replace("`", "");
        } else {
            return "";
        }
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

