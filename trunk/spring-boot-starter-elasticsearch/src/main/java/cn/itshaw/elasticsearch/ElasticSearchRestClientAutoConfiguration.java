package cn.itshaw.elasticsearch;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * User: itshaw
 * Date: 2016/9/25
 * Time: 11:20
 */
@Configuration
@EnableConfigurationProperties(ElasticSearchRestClientProperties.class)
@ConditionalOnClass(ElasticSearchRestClientProperties.class)
public class ElasticSearchRestClientAutoConfiguration {

    @Autowired
    private ElasticSearchRestClientProperties elasticSearchRestClientProperties;

    @Bean
    @ConditionalOnMissingBean(ElasticSearchRestClientProperties.class)
    public RestHighLevelClient restClient(){
        RestHighLevelClient restHighLevelClient ;
        String[] hosts =  elasticSearchRestClientProperties.getClusterNodes().split(",");

        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for(int i=0 ; i<hosts.length;i++){
            String[] ipPort = hosts[i].split(":");
            httpHosts[i] = new HttpHost(ipPort[0], Integer.parseInt(ipPort[1]),elasticSearchRestClientProperties.getScheme());
        }
        RestClientBuilder builder = RestClient.builder(httpHosts);

        String[] headers =  elasticSearchRestClientProperties.getHeader().split(",");
        Header[] defaultHeaders = new Header[headers.length];
        for(int i=0 ; i<headers.length;i++){
            String[] headerKeyValue = headers[i].split(":");
            defaultHeaders[i] = new BasicHeader(headerKeyValue[0],headerKeyValue[1]);
        }

        //Set the default headers that need to be sent with each request, to prevent having to specify them with each single request
        builder.setDefaultHeaders(defaultHeaders);
        //Set the timeout that should be honoured in case multiple attempts are made for the same request. The default value is 30 seconds,
        builder.setMaxRetryTimeoutMillis(elasticSearchRestClientProperties.getMaxRetryTimeoutMillis());
        builder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(HttpHost host) {

            }
        });
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setSocketTimeout(elasticSearchRestClientProperties.getSocketTimeout());
            }
        });
        RestClient restClient =  builder.build();
        restHighLevelClient = new RestHighLevelClient(restClient);
        return restHighLevelClient;
    }

}
