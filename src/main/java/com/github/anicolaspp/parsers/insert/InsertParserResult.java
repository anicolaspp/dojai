package com.github.anicolaspp.parsers.insert;

import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.select.SelectField;
import lombok.Getter;
import lombok.Setter;
import org.ojai.Document;
import org.ojai.store.Query;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public class InsertParserResult extends ParserQueryResult<Document> {

    public InsertParserResult(Query query, String table, List<SelectField> selectFields, Boolean successful, ParserType type, Stream<Document> documents) {
        super(query, table, selectFields, successful, type, documents);
    }
}

