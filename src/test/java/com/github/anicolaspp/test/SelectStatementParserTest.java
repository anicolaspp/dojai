package com.github.anicolaspp.test;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import lombok.val;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

public class SelectStatementParserTest implements JavaOjaiTesting {

    @Test
    public void testSelect() throws Exception {

        val sql = "select a, b from /user/mapr/t1 where a = 5 or b <= 10  limit 10";

        val statement = CCJSqlParserUtil.parse(sql);

        assert statement instanceof Select;

    }


}


