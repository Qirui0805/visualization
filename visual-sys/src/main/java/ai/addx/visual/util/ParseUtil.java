package ai.addx.visual.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

public class ParseUtil {
    public static  MultiValueMap<String, String> parseForBatteryInfo(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONObject("hits").getJSONArray("hits");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        LinkedList<String> times = new LinkedList<>();
        LinkedList<String> batteries = new LinkedList<>();
        for (Object object : jsonArray) {
            JSONObject jsonObject1 = (JSONObject)object;
            JSONObject source = jsonObject1.getJSONObject("_source");
            times.addFirst(source.getString("@timestamp"));
            String message = source.getString("message");
            String battery = "";
            int i = message.length() - 1; char c;
            while ((c = message.charAt(i--)) != ' ') {
                battery = c + battery;
            }
            batteries.addFirst(battery);
        }
        map.put("time", times);
        map.put("battery", batteries);
        System.out.println("get return jason data");
        return map;
    }

    public static String getSerialNumber(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        String uid = jsonObject.getJSONObject("data").getString("uid");
        return uid;
    }

}
