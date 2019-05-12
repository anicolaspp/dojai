package com.github.anicolaspp.test;

import com.github.anicolaspp.parsers.ChainParser;
import com.github.anicolaspp.parsers.InsertStatementParser;
import com.github.anicolaspp.parsers.SelectStatementParser;
import lombok.val;
import org.junit.Test;

public class ChainParserTest {
    
    @Test
    public void shouldBuildSelectStatementParser() {
    
        val parser = ChainParser.build(null);
        
        assert parser instanceof SelectStatementParser;
        
        assert parser.next() instanceof InsertStatementParser;
    }
    
    
}

