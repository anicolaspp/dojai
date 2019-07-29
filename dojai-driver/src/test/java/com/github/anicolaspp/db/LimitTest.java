package com.github.anicolaspp.db;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import lombok.val;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.statement.select.Limit;
import org.junit.Test;

import java.util.Random;
import java.util.stream.Stream;

public class LimitTest implements JavaOjaiTesting {

    @Test
    public void testNullLimit() {
        Limit nullLimit = null;

        val queryLimit = QueryLimit.limit(nullLimit);

        val emptyQuery = connection().newQuery();

        val queryWithLimit = queryLimit.applyTo(emptyQuery);

        assert queryWithLimit == emptyQuery;
    }

    @Test
    public void testRandomLimit() {
        val limit = new Random().nextInt(10);

        Limit theLimit = new Limit();
        theLimit.setRowCount(new LongValue(limit));

        val query = QueryLimit.limit(theLimit).applyTo(connection().newQuery());

        assert query.asJsonString().equals(connection().newQuery().limit(limit).asJsonString());
    }
}

