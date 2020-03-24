package ai.addx.visual.util;

import ai.addx.visual.bean.info.*;
import ai.addx.visual.exception.QueryException;
import ai.addx.visual.factory.ClientFactory;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class SearchUtil {

    private static Logger logger = LoggerFactory.getLogger(SearchUtil.class);
    //max number of threads
    private static int MAX_THREAD = Runtime.getRuntime().availableProcessors();
    //thread pool
    private static ExecutorService executorService = new ThreadPoolExecutor(MAX_THREAD, Integer.MAX_VALUE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    //mapper of environment and corresponding indices
    private static Map<String, String> logIndices = new HashMap<>();
    private static Map<String, String> filebeatIndices = new HashMap<>();
    //query parameters
    private static final int SLICE_MAX = 3;
    private static final int FROM = 0;
    private static final int SIZE = 10000;

    static {
        logIndices.put("test", "log-test*");
        logIndices.put("staging", "log-stage*");
        logIndices.put("prod", "log-prod*");

        filebeatIndices.put("staging", "filebeat-stage*");
        filebeatIndices.put("prod", "filebeat-prod*");
    }

    private static BoolQueryBuilder createBoolQueryBuilder(String serialNumber, String[] must, String[] should) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (must.length > 0) {
            for (String text : must) {
                boolQuery.must(QueryBuilders.matchPhraseQuery("message", text));
            }
        }
        if (should.length > 0) {
            for (String text : should) {
                boolQuery.should(QueryBuilders.matchPhraseQuery("message", text));
            }
            boolQuery.minimumShouldMatch(1);
        }
        if (!StringUtils.isEmpty(serialNumber)) {
            MatchQueryBuilder matchQuery1 = QueryBuilders.matchQuery("message", serialNumber);
            boolQuery.must(matchQuery1);
        }
        return boolQuery;
    }

    private static void clearScroll(RestHighLevelClient client, List<String> scrollIds) {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.setScrollIds(scrollIds);
        ClearScrollResponse clearScrollResponse = null;
        try {
            clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        boolean succeeded = clearScrollResponse.isSucceeded();
    }

    private static SearchRequest buildSearchRequest (int sliceId, String environment, String serialNumber, String from, String to, String[] must, String[] should) {

        //bool query
        BoolQueryBuilder boolQuery = createBoolQueryBuilder(serialNumber, must, should);
        boolQuery.filter(QueryBuilders.rangeQuery("@timestamp").from(from).to(to));
        //source
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().from(FROM).size(SIZE);
        sourceBuilder.query(boolQuery).
//                                sort("@timestamp", SortOrder.ASC).
                        fetchSource(new String[]{"@timestamp", "message"}, null);
        //slice
        SliceBuilder sliceBuilder = new SliceBuilder(sliceId, SLICE_MAX);
        sourceBuilder.slice(sliceBuilder);
        //request body
        SearchRequest request = new SearchRequest(environment);
        request.source(sourceBuilder)
                .scroll(TimeValue.timeValueSeconds(60));
        return request;
    }

    private static List<SearchHit> searchExecutor(String country, String environment, String serialNumber, String from, String to, String[] must, String[] should, long interval) {
        ReentrantLock lock = new ReentrantLock();
        //result collection
        List<SearchHit> res = new ArrayList<>();
        //split time range
        List<String> timeInterval = DateUtil.split(from, to, DateUtil.T_LONDON, interval);
        logger.info("Time Interval {}",timeInterval.toString());
        //client
        RestHighLevelClient client = ClientFactory.getClient(country);
        //thread pool
        CountDownLatch countDownLatch = new CountDownLatch((timeInterval.size() - 1) * SLICE_MAX);
        //concurrently search for each time interval
        for (int i = 0; i < timeInterval.size() - 1; i++) {
            final String start = timeInterval.get(i);
            final String end = timeInterval.get(i + 1);
            executorService.submit(() -> {
                //spread each slice to an independent thread
                for (int j = 0; j < SLICE_MAX; j++) {
                    final int index = j;
                    executorService.submit(() -> {
                        int sliceId = index;
                        //request body
                        SearchRequest request = buildSearchRequest(sliceId, environment, serialNumber, start, end, must, should);
                        logger.info(request.toString());
                        try {
                            //do searching
                            logger.info("Start Query Process");
                            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                            //get scroll id
                            List<String> scrollIds = new ArrayList<>();
                            String scrollId = response.getScrollId();
                            scrollIds.add(scrollId);

                            SearchHit[] searchHits = response.getHits().getHits();
                            int result = searchHits.length;
                            res.addAll(Arrays.asList(searchHits));
                            logger.info("Total Response From Slice {}: {} Time Range From {} To {}" , sliceId, response.getHits().getTotalHits().value, start, end);
                            logger.info("First Time Response {} Time Range From {} To {}", result, start, end);
                            logger.info("Need Further Search? {}", result == SIZE);

                            while (result == SIZE) {
                                //scroll search
                                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId).scroll(TimeValue.timeValueMinutes(1));
                                response = client.scroll(scrollRequest, RequestOptions.DEFAULT);

                                scrollId = response.getScrollId();
                                scrollIds.add(scrollId);
                                searchHits = response.getHits().getHits();
                                result = searchHits.length;
                                logger.info("Scroll Search Response {} Time Range From {} To {}", searchHits.length, start, end);
                                synchronized (res) {
                                    res.addAll(Arrays.asList(searchHits));
                                }
                            }
                            //clear scroll
                           clearScroll(client, scrollIds);
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                            e.printStackTrace();
                            throw new QueryException("连接失败", e);
                        } finally {
                            logger.info("Finish One Task");
                            countDownLatch.countDown();
                        }
                    });
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<BatteryDTO> searchBattery(String country, String environment, String serialNumber, String from, String to){
        List<SearchHit> searchHits = searchExecutor(country, logIndices.get(environment), serialNumber, from, to, new String[]{"Current battery"}, new String[]{}, DateUtil.DAY * 30);
        return ResponseUtil.parseForBatteryInfo(searchHits);
    }
    public static List<Incident> searchIncident(String country, String environment, String serialNumber, String from, String to) throws RuntimeException{
        List<SearchHit> searchHits = searchExecutor(country, logIndices.get(environment), serialNumber, from, to, new String[]{}, new String[]{"is awake with POR", "New video captured by"}, DateUtil.DAY * 30);
        return ResponseUtil.parseForIncident(searchHits);
    }

    public static Map<String, Integer> searchAPI(String country, String environment, String from, String to) {
//        List<SearchResponse> ack = searchExecutor(country, environment, null, from, to, new String[]{"Rest API"}, new String[]{});
        List<SearchHit> response = searchExecutor(country, logIndices.get(environment), null, from, to, new String[]{"Rest API"}, new String[]{}, DateUtil.HOUR * 3);
        return ResponseUtil.parseForAPIRate(response);
    }

    public static List<ConnectStatusDTO> searchConnect(String country, String environment, String serialNumber, String from, String to) {
        List<SearchHit> searchHits = searchExecutor(country, logIndices.get(environment), serialNumber, from, to, new String[]{}, new String[]{"Reply heartbeat", "Updating hardware"}, DateUtil.DAY * 30);
        return ResponseUtil.parseForConnect(searchHits);
    }

    private static List<SearchHit> searchLiveRequest(String country, String environment, String serialNumber, String from, String to) {
        return searchExecutor(country, logIndices.get(environment), serialNumber, from, to, new String[] {"Start live request"}, new String[]{}, DateUtil.DAY * 30);
    }

    private static Map<String, String> searchLiveResponse(String country, String environment, String serialNumber, String from, String to) {
        List<SearchHit> searchHits = searchExecutor(country, logIndices.get(environment), serialNumber, from, to, new String[]{"received startStream"}, new String[]{}, DateUtil.DAY * 30);
        return ResponseUtil.parseForLiveResponse(searchHits);
    }

    public static List<LiveDTO> searchLiveStatus(String country, String environment, String serialNumber, String from, String to) {
        List<SearchHit> request = SearchUtil.searchLiveRequest(country, environment, serialNumber, from, to);
        Map<String, String> response = SearchUtil.searchLiveResponse(country, environment, serialNumber, from, to);
        return ResponseUtil.parseForLiveStatus(request, response);
    }


    public static LiveDTO searchSingleLiveStatus(String country, String environment, String serialNumber, String from, String to) {
        List<SearchHit> request = SearchUtil.searchLiveRequest(country, environment, serialNumber, from, to);
        Map<String, String> response = SearchUtil.searchLiveResponse(country, environment, serialNumber, from, to);
        return ResponseUtil.parseForSingleLiveStatus(request, response);
    }

    public static List<DeviceCmd> searchCmd(String country, String environment, String serialNumber, String from, String to) {
        List<SearchHit> cmdResponse = new ArrayList<>();
        List<SearchHit> ackResponse = new ArrayList<>();
        Future<List<SearchHit>> cmdThread = executorService.submit(() -> searchExecutor(
                country,
                logIndices.get(environment),
                serialNumber,
                from,
                to,
                new String[]{"cmd"},
                new String[]{},
                DateUtil.DAY * 4));
        Future<List<SearchHit>> ackThread = executorService.submit(() -> searchExecutor(
                country,
                filebeatIndices.get(environment),
                serialNumber,
                from,
                to,
                new String[]{"cmd_ack", "AddxMqttMsg"},
                new String[]{},
                DateUtil.DAY*4 ));
        try {
            cmdResponse.addAll(cmdThread.get());
            ackResponse.addAll(ackThread.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Map<String, String> ack = ResponseUtil.parseForCmdAck(ackResponse);
        List<DeviceCmd> deviceCmds = ResponseUtil.parseForDeviceCmd(ack, cmdResponse);
        return deviceCmds;
    }
}
