package com.github.anicolaspp.db;

import lombok.val;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.statement.select.Limit;
import org.ojai.store.Query;

public class QueryLimit {

    private Limit limit;

    private QueryLimit(Limit limit) {

        this.limit = limit;
    }

    public static QueryLimit limit(Limit limit) {
        return new QueryLimit(limit);
    }

    public Query applyTo(Query query) {
        if (limit == null) {
            return query;
        }

        val theLimit = limit.getRowCount();

        if (theLimit instanceof LongValue) {
            query.limit(((LongValue) theLimit).getValue());

            return query;
        }

        return query;
    }
}
