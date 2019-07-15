package com.github.anicolaspp.parsers;

import com.github.anicolaspp.parsers.select.SelectField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ojai.store.Query;

import java.util.List;


@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ParserQueryResult {

    private Query query;

    private String table;

    private List<SelectField> selectFields;

    private Boolean successful;

    private ParserType type;
}

