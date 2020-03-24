package ai.addx.visual.controller;

import ai.addx.visual.bean.info.*;
import ai.addx.visual.bean.query.GeneralQueryVO;
import ai.addx.visual.bean.result.ResultVO;
import ai.addx.visual.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class QueryController {
    private Logger logger = LoggerFactory.getLogger(QueryController.class);
    @Autowired
    QueryService service;
    @PostMapping("battery")
    public ResultVO getBattery(@RequestBody GeneralQueryVO vo) {
        logger.info("Accept Battery Query Request" + "with Param:" + vo);
        List<BatteryDTO> res = service.getBatteryInfo(vo);
        logger.info("Total of result: " + res.size() + "\n" + res);
        if (res.size() == 0) {
            return new ResultVO(2, "warning", "结果为空");
        }
        return new ResultVO(0, "success", res);

    }
    @PostMapping("api")
    public ResultVO getAPI(@RequestBody GeneralQueryVO vo) {
        logger.info("Accept API Query Request " + "with Param:" + vo);
        List<APIDTO> apiRate = service.getAPIRate(vo);
        if (apiRate.size() == 0) {
            return new ResultVO(2, "warning", "结果为空");
        }
        ResultVO res = new ResultVO(0, "success",apiRate);
        logger.info("Total of result: " + apiRate.size() + "\n" + apiRate);
        return res;
    }

    @PostMapping("connect")
    public ResultVO getConnect(@RequestBody GeneralQueryVO vo) {
        logger.info("Accept Connect Status Query Request " + "with Param:" + vo);
        List<ConnectStatusDTO> connect = service.getDeviceConnect(vo);
        logger.info("Get result, total: " +connect.size() + '\n' + connect);
        if (connect.size() == 0) {
            return new ResultVO(2, "warning", "结果为空");
        }
        return new ResultVO(0, "success",connect);
    }

    @PostMapping("live")
    public ResultVO getLive(@RequestBody GeneralQueryVO vo) {
        logger.info("Accept cmd query request " + "with param:" + vo);
        if (!StringUtils.isEmpty(vo.getSerialNumber())) {
            LiveDTO status = getSingleLive(vo);
            return new ResultVO(0, "success", status);
        } else {
             List<LiveDTO> liveStatus = service.getLiveStatus(vo);
             logger.info("Get result, total devices: " +liveStatus.size() + '\n' + liveStatus);
             return new ResultVO(0, "success", liveStatus);
        }
    }

    @PostMapping("cmd")
    public ResultVO getCmd(GeneralQueryVO vo) {
        logger.info("Accept cmd query request " + "with param:" + vo);
        if (!vo.getSerialNumber().isEmpty()) {
//            LiveDTO status = getSingleLive(userSn, vo);
//            return new ResultVO(0, "success", status);
            return null;
        } else {
            CmdDTO cmd = service.getCmd(vo);
            logger.info("Get Result, Total Devices: {}, Total Cmd: {}, Total Ack: {}", cmd.getDeviceCmds().size(), cmd.getCmdCount(), cmd.getAckCount());
            return new ResultVO(0, "success", cmd);
        }
    }

    private LiveDTO getSingleLive(GeneralQueryVO vo) {
        LiveDTO res = service.getSingleLiveStatus(vo);
        logger.info("Get result, total devices: " +res.getMessages().size() + '\n' + res.getMessages());
        return res;
    }

}
