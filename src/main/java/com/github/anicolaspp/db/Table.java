package com.github.anicolaspp.db;

import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.util.TablesNamesFinder;

public class Table {

    private final String tableName;

    private Table(Statement statement) {

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        this.tableName = tablesNamesFinder
                .getTableList(statement)
                .get(0)
                .replace("`", "")
                .replace("\"", "");
    }

    private Table(FromItem from) {
        if (from instanceof net.sf.jsqlparser.schema.Table) {
            this.tableName = ((net.sf.jsqlparser.schema.Table) from)
                    .getName()
                    .replace("`", "")
                    .replace("\"", "");
        } else {
            this.tableName = "";
        }
    }

    public static Table from(Statement statement) {
        return new Table(statement);
    }

    public static Table from(FromItem from) {
        return new Table(from);
    }

    public String getName() {
        return tableName;
    }
}
