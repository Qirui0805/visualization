package ai.addx.visual.dao;

import ai.addx.visual.bean.info.*;
import ai.addx.visual.bean.query.GeneralQueryVO;
import ai.addx.visual.util.DateUtil;
import ai.addx.visual.util.RequestUtil;
import ai.addx.visual.util.SearchUtil;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Repository
public class QueryDao {

    public  List<BatteryDTO> getBatteryInfo(GeneralQueryVO batteryVO) {
        return SearchUtil.searchBattery(batteryVO.getCountry(),
                    batteryVO.getEnvironment(),
                    batteryVO.getSerialNumber(),
                    DateUtil.inTransform(batteryVO.getFrom()),
                    DateUtil.inTransform(batteryVO.getTo()));
    }
    public List<Incident> getIncident(GeneralQueryVO batteryVO){
        return SearchUtil.searchIncident(batteryVO.getCountry(),
                batteryVO.getEnvironment(),
                batteryVO.getSerialNumber(),
                DateUtil.inTransform(batteryVO.getFrom()),
                DateUtil.inTransform(batteryVO.getTo()));
    }
    public String getDeviceInfo(String url) {
        return RequestUtil.sendGetRequest(url, new HashMap<>());
    }
    //同时将time信息查询出来，可能以后有用
    public Map<String, Integer> getAPIRate(GeneralQueryVO vo) {
        return SearchUtil.searchAPI(vo.getCountry(),
                vo.getEnvironment(),
                DateUtil.inTransform(vo.getFrom()),
                DateUtil.inTransform(vo.getTo()));
    }

    public List<ConnectStatusDTO> getConnect(GeneralQueryVO vo) {
        return SearchUtil.searchConnect(vo.getCountry(),
                vo.getEnvironment(),
                vo.getSerialNumber(),
                DateUtil.inTransform(vo.getFrom()),
                DateUtil.inTransform(vo.getTo()));
    }

    public List<LiveDTO> getLiveStatus(GeneralQueryVO vo) {
        return SearchUtil.searchLiveStatus(vo.getCountry(),
                vo.getEnvironment(),
                vo.getSerialNumber(),
                DateUtil.inTransform(vo.getFrom()),
                DateUtil.inTransform(vo.getTo()));
    }

    public LiveDTO getSingleLiveStatus(GeneralQueryVO vo) {
        return SearchUtil.searchSingleLiveStatus(vo.getCountry(),
                vo.getEnvironment(),
                vo.getSerialNumber(),
                DateUtil.inTransform(vo.getFrom()),
                DateUtil.inTransform(vo.getTo()));
    }

    public List<DeviceCmd> getDeviceCmd(GeneralQueryVO vo) {
        return SearchUtil.searchCmd(vo.getCountry(),
                vo.getEnvironment(),
                vo.getSerialNumber(),
                DateUtil.inTransform(vo.getFrom()),
                DateUtil.inTransform(vo.getTo()));
    }

    public void test() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> china4 = executorService.submit(() -> {
            GeneralQueryVO vo1 = new GeneralQueryVO();
            vo1.setFrom("2019-11-22 00:00:00");
            vo1.setTo("2019-11-24 00:00:00");
            vo1.setCountry("CHINA");
            vo1.setEnvironment("log-prod-*");
            System.out.println(SearchUtil.searchLiveStatus(vo1.getCountry(),
                    vo1.getEnvironment(),
                    vo1.getSerialNumber(),
                    DateUtil.inTransform(vo1.getFrom()),
                    DateUtil.inTransform(vo1.getTo())));
        });
        Future<?> china3 = executorService.submit(() -> {
            GeneralQueryVO vo1 = new GeneralQueryVO();
            vo1.setFrom("2019-11-24 00:00:00");
            vo1.setTo("2019-11-25 00:00:00");
            vo1.setCountry("CHINA");
            vo1.setEnvironment("log-prod-*");
            System.out.println(SearchUtil.searchLiveStatus(vo1.getCountry(),
                    vo1.getEnvironment(),
                    vo1.getSerialNumber(),
                    DateUtil.inTransform(vo1.getFrom()),
                    DateUtil.inTransform(vo1.getTo())));
        });
        Future<?> china2 = executorService.submit(() -> {
            GeneralQueryVO vo1 = new GeneralQueryVO();
            vo1.setFrom("2019-11-25 00:00:00");
            vo1.setTo("2019-11-27 00:00:00");
            vo1.setCountry("CHINA");
            vo1.setEnvironment("log-prod-*");
            System.out.println(SearchUtil.searchLiveStatus(vo1.getCountry(),
                    vo1.getEnvironment(),
                    vo1.getSerialNumber(),
                    DateUtil.inTransform(vo1.getFrom()),
                    DateUtil.inTransform(vo1.getTo())));
        });
        Future<?> china1 = executorService.submit(() -> {
            GeneralQueryVO vo1 = new GeneralQueryVO();
            vo1.setFrom("2019-11-27 00:00:00");
            vo1.setTo("2019-11-29 00:00:00");
            vo1.setCountry("CHINA");
            vo1.setEnvironment("log-prod-*");
            System.out.println(SearchUtil.searchLiveStatus(vo1.getCountry(),
                    vo1.getEnvironment(),
                    vo1.getSerialNumber(),
                    DateUtil.inTransform(vo1.getFrom()),
                    DateUtil.inTransform(vo1.getTo())));
        });
        Future<?> china = executorService.submit(() -> {
            GeneralQueryVO vo1 = new GeneralQueryVO();
            vo1.setFrom("2019-11-25 00:00:00");
            vo1.setTo("2019-11-30 00:00:00");
            vo1.setCountry("CHINA");
            vo1.setEnvironment("log-prod-*");
            System.out.println(SearchUtil.searchLiveStatus(vo1.getCountry(),
                    vo1.getEnvironment(),
                    vo1.getSerialNumber(),
                    DateUtil.inTransform(vo1.getFrom()),
                    DateUtil.inTransform(vo1.getTo())));
        });
        try {
            china.get();
            china1.get();
            china2.get();
            china3.get();
            china4.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
