package com.github.anicolaspp.parsers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ojai.store.Query;


@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ParserQueryResult {
    
    private Query query;
    
    private String table;
    
    private Boolean successful;
    
    private ParserType type;
}

enum ParserType {
    SELECT,
    UNKNOWN
}