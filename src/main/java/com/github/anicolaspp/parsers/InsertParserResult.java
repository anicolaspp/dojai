package com.github.anicolaspp.parsers;

import lombok.Getter;
import lombok.Setter;
import org.ojai.Document;
import org.ojai.store.Query;

import java.util.List;

@Getter
@Setter
public class InsertParserResult extends ParserQueryResult {

    private List<Document> documents;

    public InsertParserResult(Query query, String table, List<SelectField> selectFields, Boolean successful, ParserType type, List<Document> documents) {
        super(query, table, selectFields, successful, type);
        this.documents = documents;
    }
}
