package com.github.anicolaspp.db;

import com.github.anicolaspp.ojai.JavaOjaiTesting;
import lombok.val;
import org.junit.Test;
import org.ojai.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ProjectionTest implements JavaOjaiTesting {

    @Test
    public void testNoProjections() {
        Document document = connection()
                .newDocument()
                .set("a", "a")
                .set("b", "b");

        val projection = Projection.<String>project(Collections.EMPTY_LIST, document);

        val projected = projection.apply(Function.<String>identity(), connection());

        assert document.asJsonString().equals(projected.asJsonString());
    }

    @Test
    public void testRandomProjections() {
        val rnd = new scala.util.Random();

        int numberOfColumns = rnd.nextInt(100);

        Document document = connection().newDocument();

        List<String> columns = new ArrayList<>();

        for (int i = 0; i < numberOfColumns; i++) {
            String key = rnd.nextString(10);
            String value = rnd.nextString(100);

            document.set(key, value);

            columns.add(key);
        }

        int numberOfColumnsToProject = rnd.nextInt(numberOfColumns);

        List<String> toProject = new ArrayList<>();

        for (int i = 0; i < numberOfColumnsToProject; i++) {
            int idx = rnd.nextInt(numberOfColumns) % columns.size();

            String column = columns.get(idx);

            toProject.add(column);
            columns.remove(idx);
        }

        val projected = Projection
                .project(toProject, document)
                .apply(Function.identity(), connection());

        val map = projected.asMap();

        assert map.size() == numberOfColumnsToProject;

        assert map
                .entrySet()
                .stream()
                .allMatch(entry -> document.getString(entry.getKey()).equals(entry.getValue()));
    }
}
