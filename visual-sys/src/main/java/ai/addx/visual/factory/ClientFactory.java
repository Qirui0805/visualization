package ai.addx.visual.factory;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ClientFactory {
    public static RestHighLevelClient CHINA;
    public static RestHighLevelClient US;
    static {
        //Initialize
//        CHINA = new RestHighLevelClient(RestClient.builder(new HttpHost("kibana.addx.live",9200,"http")));
//        US = new RestHighLevelClient(RestClient.builder(new HttpHost("kibana-us.addx.live",9200,"http")));
    }
    public static RestHighLevelClient getClient(String country) {
        switch (country){
            case "CHINA":
//                return CHINA;
                return new RestHighLevelClient(RestClient.builder(new HttpHost("kibana.addx.live",9200,"http")));
            case "US":
//                return US;
                return new RestHighLevelClient(RestClient.builder(new HttpHost("kibana-us.addx.live",9200,"http")));
            default:
                throw new IllegalStateException("Unexpected value: " + country);
        }
    }
}
