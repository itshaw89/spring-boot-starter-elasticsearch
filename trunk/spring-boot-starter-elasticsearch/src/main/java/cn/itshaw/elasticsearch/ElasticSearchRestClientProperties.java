package cn.itshaw.elasticsearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * User: itshaw
 * Date: 2016/9/25
 * Time: 11:20
 */
@ConfigurationProperties(prefix = "spring.data.elasticsearch")
@Data
public class ElasticSearchRestClientProperties {

    @Value("spring.data.elasticsearch.clusterName")
    private String clusterName;
    @Value("spring.data.elasticsearch.cluster-nodes")
    private String clusterNodes;
    private String scheme = "http";
    private Integer maxRetryTimeoutMillis = 10000;
    private Integer socketTimeout =10000;
    private String header;

}
