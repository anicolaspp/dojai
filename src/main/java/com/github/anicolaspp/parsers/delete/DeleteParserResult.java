package com.github.anicolaspp.parsers.delete;

import com.github.anicolaspp.parsers.ParserQueryResult;
import com.github.anicolaspp.parsers.ParserType;
import com.github.anicolaspp.parsers.select.SelectField;
import lombok.Getter;
import lombok.Setter;
import org.ojai.Value;
import org.ojai.store.Query;

import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
public class DeleteParserResult extends ParserQueryResult<Value> {

    public DeleteParserResult(Query query, String table, List<SelectField> selectFields, Boolean successful, ParserType type, Stream<Value> documents) {
        super(query, table, selectFields, successful, type, documents);
    }
}
