package ai.addx.visual.util;

import com.alibaba.fastjson.JSON;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RequestUtil {

    public static String sendPostRequest(String url, Map<String, Object> params) {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(params, headers);
//        HttpEntity entity = new HttpEntity(headers);
//        String p = JSON.toJSONString(params);
        ResponseEntity<String> res = template.exchange(url, HttpMethod.POST, entity, String.class);
        return res.getBody();
    }
    public static String sendGetRequest(String url, Map<String, Object> params) {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
//        HttpEntity entity = new HttpEntity(headers);
//        String p = JSON.toJSONString(params);
        if (!params.isEmpty()) {
            int i = 0;
            url += "?";
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                url += i++ == 0 ? "" : "&";
                url += entry.getKey() + "=" + entry.getValue();
            }
        }
        ResponseEntity<String> res = template.exchange(url, HttpMethod.GET, entity, String.class);
        return res.getBody();
    }
}
