package ai.addx.visual.service.Impl;

import ai.addx.visual.bean.info.*;
import ai.addx.visual.bean.query.GeneralQueryVO;
import ai.addx.visual.dao.QueryDao;
import ai.addx.visual.service.QueryService;
import ai.addx.visual.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class QueryServiceImpl implements QueryService {
    Logger logger = LoggerFactory.getLogger(QueryService.class);
    @Autowired
    QueryDao dao;

    @Override
    public List<BatteryDTO> getBatteryInfo(GeneralQueryVO batteryVO) {
        List<BatteryDTO> list = dao.getBatteryInfo(batteryVO);
        List<Incident> incident = dao.getIncident(batteryVO);
        return BeanUtil.mapBatteryInfoIncident(list, incident);
    }

    public List<APIDTO> getAPIRate(GeneralQueryVO vo){
        List<APIDTO> list = new LinkedList<>();
        Map<String, Integer> apiRate = dao.getAPIRate(vo);
        int sum = 0;
        for (Map.Entry<String, Integer> entry : apiRate.entrySet()) {
            list.add(new APIDTO(entry.getKey(), entry.getValue()));
            sum += entry.getValue();
        }
        logger.info("Total api: " + sum);
        return list;
    }

    @Override
    public List<ConnectStatusDTO> getDeviceConnect(GeneralQueryVO vo) {
        return dao.getConnect(vo);
    }

    @Override
    public List<LiveDTO> getLiveStatus(GeneralQueryVO vo) {
        return dao.getLiveStatus(vo);
    }

    @Override
    public LiveDTO getSingleLiveStatus(GeneralQueryVO vo) {
        return dao.getSingleLiveStatus(vo);
    }

    @Override
    public CmdDTO getCmd(GeneralQueryVO vo) {
        List<DeviceCmd> deviceCmds = dao.getDeviceCmd(vo);
        CmdDTO cmd = new CmdDTO();
        for (DeviceCmd deviceCmd : deviceCmds) {
            cmd.addDeviceCmd(deviceCmd);
        }
        return cmd;
    }


}
