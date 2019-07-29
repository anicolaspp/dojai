package com.github.anicolaspp.sql;

import java.sql.SQLException;

/**
 * This is an Alias for Function<A, B> but allows us to throw SQLException.
 *
 * @param <A>
 * @param <B>
 */
@FunctionalInterface
public interface Func<A, B> {
    B apply(A value) throws SQLException;
}