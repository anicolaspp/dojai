package com.github.anicolaspp.sql;

import java.sql.SQLException;

@FunctionalInterface
public interface Func<A, B> {
    B apply(A value) throws SQLException;
}
