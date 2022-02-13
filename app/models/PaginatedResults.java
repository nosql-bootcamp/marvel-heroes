package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.libs.Json;

import java.util.List;

public class PaginatedResults<T> {

    public final int total;
    public final int page;
    public final List<T> results;
    public final int totalPage;

    public PaginatedResults(int total, int page, int totalPage, List<T> results) {
        this.total = total;
        this.page = page;
        this.totalPage = totalPage;
        this.results = results;
    }

    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode resultsAsTree = mapper.valueToTree(results);
        return Json.newObject()
                .put("total", total)
                .put("totalPage", totalPage)
                .put("page", page)
                .set("results", resultsAsTree);
    }
}
