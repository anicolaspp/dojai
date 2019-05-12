package com.github.anicolaspp.test;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import com.github.anicolaspp.parsers.SelectStatementParser;
import lombok.val;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.junit.Test;

public class SelectStatementParserTest implements JavaOjaiTesting {
    
    @Test
    public void testSelect() throws JSQLParserException {
        
        val sql = "select a, b from t1 as tx";
        
        val parser = new SelectStatementParser(connection());
        
        val query = parser.parse(CCJSqlParserUtil.parse(sql));
    
    
        System.out.println(query);
    }
}
