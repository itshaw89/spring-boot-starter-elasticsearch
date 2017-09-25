package cn.itshaw.elasticsearch;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/document/")
@Slf4j
public class DocumentController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private final String indexName =  "sohu";

    private final String typeName =  "news";

    @GetMapping("/{id}")
    public Document get(@PathVariable String id) throws Exception {
        GetRequest getRequest = new GetRequest(
                indexName,
                typeName,
                id);
        GetResponse getResponse = restHighLevelClient.get(getRequest);
        Gson gson = new Gson();
        return gson.fromJson(getResponse.getSourceAsString(),Document.class);
    }

    @PostMapping("/")
    public String create(@RequestBody Document document) throws Exception {
        Gson gson = new Gson();
        IndexRequest request = new IndexRequest(
                indexName,
                typeName);
        request.source(gson.toJson(document), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(request);
        return indexResponse.getId();
    }

    @PutMapping("/{id}")
    public RestStatus update(@PathVariable String id,@RequestBody Document document) throws Exception {
        Gson gson = new Gson();
        UpdateRequest request = new UpdateRequest(
                indexName,
                typeName,
                id).doc(gson.toJson(document),XContentType.JSON);
        UpdateResponse updateResponse = restHighLevelClient.update(request);
        return updateResponse.status();
    }

    @DeleteMapping("/{id}")
    public RestStatus delete(@PathVariable String id) throws Exception {
        DeleteRequest request = new DeleteRequest(
                indexName,
                typeName,
                id);
        DeleteResponse deleteResponse = restHighLevelClient.delete(request);
        return deleteResponse.status();
    }

    @GetMapping("/search/{context}")
    public List<Document> search(@PathVariable String context) throws Exception {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        /* sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));*/ //精确查询
        //QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("content", context)//全文查询-单个
        QueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(context, "content","title") ////全文查询-多个
                .fuzziness(Fuzziness.AUTO)
                .prefixLength(3)
                .maxExpansions(10);
        sourceBuilder.query(matchQueryBuilder);

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        SearchHits searchHits = searchResponse.getHits();

        List<Document> documents = new ArrayList<>();
        Gson gson = new Gson();
        for (SearchHit hit : searchHits) {
            documents.add(gson.fromJson(hit.getSourceAsString(), Document.class));
        }
        return documents;
    }
}
