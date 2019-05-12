package com.github.anicolaspp.test;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import com.github.anicolaspp.parsers.Constants;
import com.github.anicolaspp.parsers.OjaiParser;
import lombok.val;
import org.junit.Test;

public class OjaiParserTest implements JavaOjaiTesting {

    @Test
    public void testEmptyQuery() {
    
        val connection = connection();
    
        OjaiParser instance = statement -> null;
        
        val empty = instance.emptyQuery(connection);
    
        assert empty.asJsonString().equals(Constants.EMPTY_QUERY);
    }
}
