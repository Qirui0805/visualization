package ai.addx.visual.service;

import ai.addx.visual.bean.info.*;
import ai.addx.visual.bean.query.GeneralQueryVO;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


public interface QueryService {

    public List<BatteryDTO> getBatteryInfo(GeneralQueryVO batteryVO);
//    public String getSerialNumber(String userSn, String environment, String country);
    public List<APIDTO> getAPIRate(GeneralQueryVO vo);
    public List<ConnectStatusDTO> getDeviceConnect(GeneralQueryVO vo);
    public List<LiveDTO> getLiveStatus(GeneralQueryVO vo);
    public LiveDTO getSingleLiveStatus(GeneralQueryVO vo);
    public CmdDTO getCmd(GeneralQueryVO vo);
}
