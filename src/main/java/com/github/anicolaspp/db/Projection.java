package com.github.anicolaspp.db;

import javafx.util.Pair;
import lombok.val;
import org.ojai.Document;
import org.ojai.store.Connection;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Projection<A> {

    private final Document document;
    private final List<A> columns;

    private Projection(Document document, List<A> columns) {
        this.document = document;
        this.columns = columns;
    }

    public static <A> Projection<A> project(List<A> columns, Document document) {

        return new Projection<>(document, columns);
    }

    public Document apply(Function<A, String> columnNameExtractor, Connection connection) {
        if (columns.size() == 0) {
            return connection.newDocument(document);
        }

        Map<String, Object> docMap = columns
                .stream()
                .map(column -> {
                    val columnName = columnNameExtractor.apply(column);

                    val value = document.getValue(columnName);

                    return new Pair<>(columnName, value);
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        return connection.newDocument(docMap);
    }
}
