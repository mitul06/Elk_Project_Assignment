package com.practice.assignmentelk.Controller;

import com.practice.assignmentelk.Domain.Bank;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class EsController {

    RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost("localhost", 9200, "http"))
    );

    // Part 1

    @PostMapping("/bankdata/{id}")
    public void saveBankDetails(@RequestBody Bank bank,
                                @PathVariable String id) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("depoid", bank.getDepoid());
        map.put("depoName", bank.getDepoName());
        map.put("branchName", bank.getBranchName());
        map.put("depoAmt", bank.getDepoAmt());
        map.put("depoDate", bank.getDepoDate());
        map.put("custName", bank.getCustName());
        map.put("custCity", bank.getCustCity());

        IndexRequest indexRequest = new IndexRequest("bank", "doc", id).source(map);
        IndexResponse indexResponse = client.index(indexRequest);
        System.out.println("-----Save Bank Data-----");
    }

    @GetMapping("/getall/{id}")
    public Map<String, Object> getAll(@PathVariable String id) throws IOException {
        GetRequest getRequest = new GetRequest("bank");
        getRequest.id(id);
        GetResponse getResponse = client.get(getRequest);
        return getResponse.getSource();
    }

    @GetMapping("/getcustname/{custName}")
    public List<Map<String, Object>> searchByName(@PathVariable String custName) throws IOException {

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("custName", custName)
                .operator(Operator.AND);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String, Object>> list = Arrays.stream(searchHits).map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());

        return list;
    }

    @GetMapping("/getnameandamt")
    public List<Object> searchByNameAndAmt(@RequestParam(name = "custName") String custName,
                                           @RequestParam(name = "depoAmt") Integer depoAmt) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("custName", custName));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        String custCity = (String) searchResponse.getHits().getAt(0).getSourceAsMap().get("custCity");
        searchSourceBuilder.query(QueryBuilders.matchQuery("custCity", custCity));
        searchRequest.source(searchSourceBuilder);
        searchResponse = client.search(searchRequest);

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<String> depoNames = new ArrayList<>();

        for (SearchHit hit : hits) {
            depoNames.add((String) hit.getSourceAsMap().get("depoName"));
        }

        List<Object> results = new ArrayList<>();

        SearchRequest searchRequest1 = new SearchRequest("bank");
        searchRequest1.types("doc");
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();


        for (String depoName : depoNames) {
            searchSourceBuilder1.query(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("depoName", depoName))
                    .must(QueryBuilders.rangeQuery("depoAmt").gt(depoAmt)));

            searchRequest1.source(searchSourceBuilder1);
            SearchResponse searchResponse1 = client.search(searchRequest1);

            searchResponse1.getHits().forEach(hit -> results.add(hit.getSourceAsMap().get("custName")));

        }

        return results;
    }

    @GetMapping("/depodtails")
    public List<Map<String, Object>> sameCityDepoDetails(@RequestParam(name = "custCity") String custCity) throws IOException {

        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQueryBuilder = QueryBuilders
                .matchQuery("custCity", custCity)
                .operator(Operator.AND);

        searchSourceBuilder.query(matchQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String, Object>> list = Arrays.stream(searchHits).map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());

        return list;

    }

    @GetMapping("/depo")
    public List<String> getSameBranchAndCustCity(@RequestParam(name = "branchName") String branchName,
                                                 @RequestParam(name = "custCity") String custCity) throws IOException {

        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("branchName", branchName));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchRequest searchRequest1 = new SearchRequest("bank");
        searchRequest1.types("doc");

        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(QueryBuilders.matchQuery("custCity", custCity));

        searchRequest1.source(searchSourceBuilder1);
        SearchResponse searchResponse1 = client.search(searchRequest1);

        System.out.println("searchResponse1 :" + searchResponse1);
        System.out.println("searchResponse :" + searchResponse);

        List<Object> branchCityName = new ArrayList<>();
        List<Object> custCityName = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        SearchHit[] hits1 = searchResponse1.getHits().getHits();


        for (SearchHit hit : hits) {
            branchCityName.add((String) hit.getSourceAsMap().get("branchName"));
        }

        for (SearchHit hit : hits1) {
            custCityName.add((String) hit.getSourceAsMap().get("custCity"));
        }

        System.out.println("branchCityName" + branchCityName);
        System.out.println("custCityName " + custCityName);

        List<String> results = new ArrayList<>();


        if (branchCityName != custCityName) {
            return results;
        }
        return results;
    }

    @GetMapping("/depositer")
    public List<Object> getDepoNameAndDepoAmt(@RequestParam(name = "custName") String custName,
                                              @RequestParam(name = "depoAmt") Integer depoAmt) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("custName", custName));
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        String custCity = (String) searchResponse.getHits().getAt(0).getSourceAsMap().get("custCity");
        searchSourceBuilder.query(QueryBuilders.matchQuery("custCity", custCity));
        searchRequest.source(searchSourceBuilder);
        searchResponse = client.search(searchRequest);

        SearchHit[] hits = searchResponse.getHits().getHits();
        List<String> depoNames = new ArrayList<>();

        for (SearchHit hit : hits) {
            depoNames.add((String) hit.getSourceAsMap().get("depoName"));
        }

        List<Object> results = new ArrayList<>();

        SearchRequest searchRequest1 = new SearchRequest("bank");
        searchRequest1.types("doc");
        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();


        for (String depoName : depoNames) {
            searchSourceBuilder1.query(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchQuery("depoName", depoName))
                    .must(QueryBuilders.rangeQuery("depoAmt").gt(depoAmt)));

            searchRequest1.source(searchSourceBuilder1);
            SearchResponse searchResponse1 = client.search(searchRequest1);

            searchResponse1.getHits().forEach(hit -> results.add(hit.getSourceAsMap().get("custName")));

        }

        return results;
    }

    @GetMapping("/samecitynames")
    public List<Object> getCustCityAndLivingCity(@RequestParam(name = "custName") String custName,
                                                 @RequestParam(name = "depoName") String depoName) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("custName", custName));

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);

        SearchRequest searchRequest1 = new SearchRequest("bank");
        searchRequest1.types("doc");

        SearchSourceBuilder searchSourceBuilder1 = new SearchSourceBuilder();
        searchSourceBuilder1.query(QueryBuilders.matchQuery("depoName", depoName));

        searchRequest1.source(searchSourceBuilder1);
        SearchResponse searchResponse1 = client.search(searchRequest1);

        SearchHit[] hits = searchResponse.getHits().getHits();
        SearchHit[] hits1 = searchResponse1.getHits().getHits();

        List<Object> list1 = new ArrayList<>();
        List<Object> list2 = new ArrayList<>();

        for (SearchHit hit : hits) {
            list1.add(hit.getSourceAsMap().get("custCity"));
        }

        for (SearchHit hit : hits1) {
            list2.add(hit.getSourceAsMap().get("custCity"));
        }

        List<Object> results = new ArrayList<>();

        if (list1 == list2) {
            results.add(list1.get(0));
            results.add(list2.get(0));

            return results;

        } else {
            return results;
        }
    }

    @GetMapping("/yeartns")
    public String getByYearTransactions(@RequestParam(name = "year") String year) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("depoDate")
                .gte(year + "-01-01")
                .lte(year + "-12-31")
                .format("yyyy-MM-dd"));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        return searchResponse.toString();

    }

    // Part 2

    @GetMapping("/cust")
    public SearchResponse getTotalByCustCity(@RequestParam(name = "custCity") String custCity) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        Bank bank = new Bank();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("custCity", custCity))
                .aggregation(AggregationBuilders.sum("total").field("depoAmt"));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        return searchResponse;
    }

    @GetMapping("/custnamebysamecity")
    public SearchResponse getTotalByCustCitySame(@RequestParam(name = "custName") String custName,
                                                 @RequestParam(name = "custCity") String custCity) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        Bank bank = new Bank();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("custName", custName))
                .query(QueryBuilders.matchQuery("custCity", custCity))
                .aggregation(AggregationBuilders.sum("total").field("depoAmt"));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);

        return searchResponse;
    }

    @GetMapping("/custnamwithdepolist")
    public SearchResponse getCustCityNameWithDeposit(@RequestParam(name = "custCity") String custCity) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        Bank bank = new Bank();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder
                .query(QueryBuilders.matchQuery("custCity", custCity))
                .aggregation(AggregationBuilders.terms("custAmt")
                        .field("depoAmt")
                        .subAggregation(
                                AggregationBuilders
                                        .sum("sum")
                                        .field("depoAmt")
                        )
                );


        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);

        return searchResponse;
    }

    @GetMapping("/getbycustyear")
    public String getCustByYear(@RequestParam(name = "dapoDate") String depoDate) throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.rangeQuery("depoDate")
                .gte(depoDate)
                .format("yyyy-MM-dd"));

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest);
        return searchResponse.toString();
    }
}
