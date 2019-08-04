package com.github.anicolaspp.db;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.ojai.store.QueryCondition;

import java.util.Collections;

public class QueryConditionBuilderTest implements JavaOjaiTesting {

    @BeforeAll
    private void init() {

    }

    @Test
    public void testEmptyQueryCondition() {

        QueryCondition queryCondition = QueryConditionBuilder
                .from(null)
                .buildWith(connection(), Collections.emptyList());

        assert queryCondition.isEmpty();

        assert queryCondition.asJsonString().equals(connection().newCondition().build().asJsonString());
    }

    @Test
    public void testSimpleWhere() {

        String where = "SELECT * FROM table WHERE x = 5";

        Expression exp = new EqualsTo();
    }

}
