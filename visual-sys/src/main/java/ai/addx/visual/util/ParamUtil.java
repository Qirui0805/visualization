package ai.addx.visual.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class ParamUtil {
    public static Map<String, Object> generateParam(String serialNumber, String from, String to) {
        Map<String, Object> map = new HashMap<>();
        //source
        Map<String, Object> source = new HashMap<>();
        List<String> includes = new ArrayList<>();
        includes.add("@timestamp");
        includes.add("message");
        source.put("includes", includes);

        //sort
        List<Map<String,String> > sort = new ArrayList<>();
        Map<String, String> members = new HashMap<>();
        members.put("@timestamp", "asc");
        sort.add(members);
        //query
        //query.bool.must
        Map<String, Object> query = new HashMap<>();
        Map<String, List> bool = new HashMap<>();
        List<Map<String,Map<String, String>> > must = new ArrayList<>();
        Map<String,Map<String, String>> m1 = new HashMap<>();
        Map<String,Map<String, String>> m2 = new HashMap<>();
        Map<String, String> match1 = new HashMap<>();
        match1.put("message", serialNumber);
        Map<String, String> match2 = new HashMap<>();
        match2.put("message", "Current");
        m1.put("match", match1);
        m2.put("match", match2);
        must.add(m1);
        must.add(m2);
        //query.bool.filter
        List<Map<String,Object> > filter = new ArrayList<>();
        Map<String, Object> f = new HashMap<>();
        Map<String, Object> range = new HashMap<>();
        Map<String, String> timestamp = new HashMap<>();
        timestamp.put("gte", from);
        timestamp.put("lt", to);
        range.put("@timestamp", timestamp);
        f.put("range", range);
        filter.add(f);
        bool.put("must",must);
        bool.put("filter", filter);
        query.put("bool", bool);

        //merger
        map.put("_source", source);
        map.put("sort", sort);
        map.put("query", query);
        map.put("size", 10000);
        map.put("from", 0);
        System.out.println("finish generating parameters");
        return map;
    }
}
