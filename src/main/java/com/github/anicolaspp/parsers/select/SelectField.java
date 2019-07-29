package com.github.anicolaspp.parsers.select;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.sf.jsqlparser.expression.Alias;

@Data
@AllArgsConstructor
public
class SelectField {
    private String name;
    private String alias;

    public String getValue() {
        return this.getAlias() == null ? this.getName() : this.getAlias();
    }

    public static SelectField withName(String name) {
        return new SelectField(name, null);
    }

    public SelectField andAlias(Alias alias) {
        if (alias != null) {
            this.alias = alias.getName();
        }

        return this;
    }
}
