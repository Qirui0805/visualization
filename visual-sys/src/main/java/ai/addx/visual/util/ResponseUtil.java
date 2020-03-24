package ai.addx.visual.util;

import ai.addx.visual.bean.info.*;
import ai.addx.visual.exception.QueryException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class ResponseUtil {
        static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);
        private static int INTERVAL = 30;
        private static int ID_LENGTH = 32;
        private static void sort(List<SearchHit> hits) {
            hits.sort((o1, o2) -> {
                Map<String, Object> source1 = o1.getSourceAsMap();
                String time1 = DateUtil.outTransform((String)source1.get("@timestamp"));
                Map<String, Object> source2 = o2.getSourceAsMap();
                String time2 = DateUtil.outTransform((String)source2.get("@timestamp"));
                return (int)DateUtil.compare(time1, time2);
            });
        }
        static List<BatteryDTO> parseForBatteryInfo(List<SearchHit> hits) {
//        SearchHits hits = response.getHits();
        logger.info("Total Response: " + hits.size());
        if (hits.size() == 0) {
            throw new QueryException("结果为空");
        }
        List<BatteryDTO> batteryInfoList = new ArrayList<>();
        for (SearchHit hit : hits) {
            Map<String, Object> source = hit.getSourceAsMap();
//            times.add(DateUtil.outTransform((String)source.get("@timestamp")));
            String time = DateUtil.outTransform((String) source.get("@timestamp"));
            String message = (String)source.get("message");
            String battery = "";
            int i = message.length() - 1; char c;
            while ((c = message.charAt(i--)) != ' ') {
                battery = c + battery;
            }
//            batteries.add(battery);
            BatteryDTO batteryInfo = new BatteryDTO(time,battery);
            batteryInfoList.add(batteryInfo);
        }
        batteryInfoList.sort((o1, o2) -> (int) DateUtil.compare(o1.getTime(), o2.getTime()));
        return batteryInfoList;
    }

        static List<Incident> parseForIncident(List<SearchHit> hits) {
            logger.info("Total Response: " + hits.size());
            List<Incident> incidents = new ArrayList<>();

            for (SearchHit hit : hits) {
                Incident incident = new Incident();
                Map<String, Object> source = hit.getSourceAsMap();
                String time = DateUtil.outTransform((String) source.get("@timestamp"));
                String[] strings = ((String)source.get("message")).split(" ");
                incident.setTime(time);
                if (strings[0].equals("Device")) {
                    incident.setMessage("设备被唤醒");
                    int por = Integer.valueOf(strings[strings.length-1]);
                    incident.setPor(por);
                } else {
                    incident.setMessage("新视频");
                }
                incidents.add(incident);
            }
            incidents.sort((o1, o2) -> (int)DateUtil.compare(o1.getTime(), o2.getTime()));

            int awakenCnt = 0, videoCnt = 0;
            Map<Integer, Integer> porCount = new HashMap<>();
            for (Incident incident : incidents) {
                if (incident.getMessage().equals("设备被唤醒")) {
                    int por = incident.getPor();
                    incident.setAwakenCount(++awakenCnt);
                    incident.setVideoCount(videoCnt);
                    if (!porCount.containsKey(por)) {
                        porCount.put(por,0);
                    }
                    porCount.put(por, porCount.get(por)+ 1);
                    incident.setPorCount(new HashMap(porCount));
                } else {
                    incident.setAwakenCount(awakenCnt);
                    incident.setVideoCount(++videoCnt);
                    incident.setPorCount(new HashMap(porCount));
                }
            }
            return incidents;
        }

        public static Map<String, Integer> parseForAPIRate(List<SearchHit> hits) {
            Map<String, Integer> map = new HashMap<>();
//            for (SearchResponse ack : responses) {
//                SearchHits hits = ack.getHits();
//                if (hits.getTotalHits().value == 0) {
//                    throw new QueryException("结果为空");
//                }
            logger.info("Total API Hits {}", hits.size());
                for (SearchHit hit : hits) {
                    Map<String, Object> source = hit.getSourceAsMap();
                    String message = ((String) source.get("message"));
                    String api = message.substring(message.indexOf("http"), message.indexOf("Body") - 1);
                    if (!map.containsKey(api)) {
                        map.put(api, 0);
                    }
                    map.put(api, map.get(api) + 1);
                }
//            }
            return map;
        }

        public static List<ConnectStatusDTO> parseForConnect(List<SearchHit> hits) {
            ResponseUtil.sort(hits);
            List<ConnectStatusDTO> list = new LinkedList<>();
            logger.info("Total Response: " + hits.size());
            if (hits.size() == 0) {
                throw new QueryException("结果为空");
            }
            long last = 0;
            for (SearchHit hit : hits) {
                Map<String, Object> source = hit.getSourceAsMap();
                String time = DateUtil.outTransform((String)source.get("@timestamp"));
                long seconds = DateUtil.getTime(time, DateUtil.DEFAULT_SHANGHAI);
                long temp = last + INTERVAL * 1000;
//                if (last > 0) {
                while (temp <= seconds) {
                    list.add(new ConnectStatusDTO(DateUtil.getString(temp, DateUtil.DEFAULT_SHANGHAI), "Offline", 0));
                    temp += INTERVAL * 1000;
                }
//                }
                last = seconds;
                String[] split = ((String) source.get("message")).split(" ");
                String message = split[0].equals("Updating") ? "Updating hardware Info" : "Reply heartbeat";
                list.add(new ConnectStatusDTO(DateUtil.getString(seconds, DateUtil.DEFAULT_SHANGHAI), message, 1));
            }
            return list;
        }

        public static List<LiveDTO>  parseForLiveStatus(List<SearchHit> request, Map<String, String> response) {
            logger.info("Total Request: " + request.size());
            //理论上如果这个方法执行的话结果不会为空，因为调用这个方法前先取了response
            if (request.size() == 0) {
                throw new QueryException("结果为空");
            }
            Map<String, List<LiveRecord>> live = new HashMap<>();
            Map<String, Integer> requestCount = new HashMap<>();
            Map<String, Integer> responseCount = new HashMap<>();
            for (SearchHit hit : request) {
                Map<String, Object> source = hit.getSourceAsMap();
                //解析数据
                String[] message = ((String)source.get("message")).split(" ");
                String id = message[3];
                String serialNumber = message[7];
                String time = DateUtil.outTransform((String)source.get("@timestamp"));
                //
                if (!live.containsKey(serialNumber)) {
                    live.put(serialNumber, new ArrayList<>());
                }
                List<LiveRecord> curr = live.get(serialNumber);
                if(!requestCount.containsKey(serialNumber)) {
//                    System.out.println(serialNumber);
                    requestCount.put(serialNumber, 0);
                }
                if (!responseCount.containsKey(serialNumber)) {
                    responseCount.put(serialNumber, 0);
                }
                requestCount.put(serialNumber, requestCount.get(serialNumber) + 1);
                if (response.containsKey(id)) {
                    responseCount.put(serialNumber, responseCount.get(serialNumber) + 1);
                    curr.add(new LiveRecord(id, serialNumber, time, response.get(id), 0));
                } else {
                    curr.add(new LiveRecord(id, serialNumber,time));
                }
            }
            List<LiveDTO> list = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : requestCount.entrySet()) {
                String serial = entry.getKey();
                list.add(new LiveDTO(serial, live.get(serial), requestCount.get(serial), responseCount.get(serial)));
            }
            return list;
        }

        public static Map<String, String> parseForLiveResponse(List<SearchHit> hits) {
            logger.info("Total Response: " + hits.size());
            if (hits.size() == 0) {
                throw new QueryException("结果为空");
            }
            Map<String, String> map = new HashMap<>();
            for (SearchHit hit : hits) {
                Map<String, Object> source = hit.getSourceAsMap();
                String id = ((String) source.get("message")).split(" ")[10];
                id = id.substring(0,id.length() - 1);
                String time = DateUtil.outTransform((String)source.get("@timestamp"));
                map.put(id, time);
            }
            return map;
        }

        public static LiveDTO parseForSingleLiveStatus(List<SearchHit> request, Map<String, String> response) {
            logger.info("Total Response: " + request.size());
            if (request.size() == 0) {
                throw new QueryException("结果为空");
            }
            LiveDTO res = null;
            List<LiveRecord> list = new ArrayList<>();
            for (SearchHit hit : request) {
                Map<String, Object> source = hit.getSourceAsMap();
                //解析数据
                String[] message = ((String)source.get("message")).split(" ");
                String id = message[3];
                String serialNumber = message[7];
                String time = DateUtil.outTransform((String)source.get("@timestamp"));
                res = new LiveDTO(id, (int)request.size(), response.size());
                if (response.containsKey(id)) {
                    list.add(new LiveRecord(id, serialNumber, time, response.get(id),0));
                } else {
                    list.add(new LiveRecord(id, serialNumber, time));
                }
            }
            res.setMessages(list);
            return res;
        }

        public static Map<String, String> parseForCmdAck(List<SearchHit> hits) {
            Map<String, String> ack = new HashMap<>();
//            for (SearchResponse ack : responses) {
//                SearchHits hits = ack.getHits();
                logger.info("Total ACK Message: " + hits.size());
//                if (hits.getTotalHits().value == 0) {
//                    throw new QueryException("结果为空");
//                }
                for (SearchHit hit : hits) {
                    Map<String, Object> source = hit.getSourceAsMap();
                    String time = DateUtil.outTransform((String) source.get("@timestamp"));
                    String message = (String) source.get("message");
                    int index = message.indexOf("id");
                    String id = message.substring(index + 5, index + 5 + ID_LENGTH);
                    ack.put(id, time);
                }
//            }
            return ack;
        }

        public static List<DeviceCmd> parseForDeviceCmd (Map<String, String> ack, List<SearchHit> hits) {
            List<DeviceCmd> res = new ArrayList<>();
            Map<String, DeviceCmd> map= new HashMap<>();
            logger.info("Total Cmd Message: " + hits.size());
            int cnt = 0;
            for (SearchHit hit : hits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
               String time = DateUtil.outTransform((String)sourceAsMap.get("@timestamp"));
               String message = (String)sourceAsMap.get("message");
               if (message.length() > 3000) {
                   cnt++;
                   continue;
               }
               String deviceId = message.substring(message.indexOf("device") + 7, message.indexOf("device") + 7 + ID_LENGTH);
               String id = message.substring(message.indexOf("id") + 5, message.indexOf("id") + 5 + ID_LENGTH);
               String type = message.substring(message.indexOf("name") + 7, message.indexOf("time") - 3);

                if (!map.containsKey(deviceId)) {
                    map.put(deviceId, new DeviceCmd(deviceId));
                }
                DeviceCmd currDevice = map.get(deviceId);
                CmdRecord record = new CmdRecord(id, deviceId, time, type);
                if (ack.containsKey(id)) {
                    record.setAckTime(ack.get(id));
                    currDevice.addBoth();
                } else {
                    currDevice.addCmd();
                }
                currDevice.addRecord(type, record);
//                if (!currDevice.getTypeRecord().containsKey(type)) {
//                    currDevice.getTypeRecord().put(type, new ArrayList<>());
//                }
//                currDevice.getTypeRecord().get(type).add(record);
            }
            System.out.println(cnt);
            for (Map.Entry<String, DeviceCmd> entry : map.entrySet()) {
                res.add(entry.getValue());
            }
            return res;
        }
}
