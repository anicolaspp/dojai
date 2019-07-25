package com.github.anicolaspp.parsers;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Table {

    private final Statement statement;

    private Table(Statement statement) {

        this.statement = statement;
    }

    public static Table from(Statement statement) {
        return new Table(statement);
    }

    public String getName() {
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();

        return tablesNamesFinder.getTableList(statement).get(0).replace("`", "");
    }
}
