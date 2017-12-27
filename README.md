# spring-boot-starter-elasticsearch
spring-boot-starter-elasticsearch makes it easier to build ElasticSearch applications that use new client


Quick Start

Wiki page for Getting Started

Maven configuration

Add the Maven dependency:


	<dependency>
	
    		<groupId>cn.itshaw</groupId>
    
   		<artifactId>spring-boot-starter-elasticsearch</artifactId>
    
    		<version>1.0.0</version>
    
	</dependency>

	<dependency>

	    <groupId>org.elasticsearch</groupId>

	    <artifactId>elasticsearch</artifactId>

	    <version>5.6.0</version>

	</dependency>

version：

	spring-boot-starter-elasticsearch  			elasticsearch

	1.0.0     		      5.6.0

default config：

	spring:

  	    data:
  
    		elasticsearch:
    
      		    cluster-name: elasticsearch
      
      		    cluster-nodes: localhost:9200
      
      		    scheme: http
      
      		    maxRetryTimeoutMillis: 10000
      
      		    socketTimeout: 10000
      
      		    header: key:value,key2:value2
      

Document：

	@Data

	@NoArgsConstructor

	@AllArgsConstructor

	public class Document {

		@Size(max = 50)
		private String title;
		@NotNull
		private String content;

		private String url;

	}

DocumentController：

    @Autowired
    private RestHighLevelClient restHighLevelClient;

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
