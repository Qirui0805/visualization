package ai.addx.visual.bean.info;

import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class DeviceCmd {
    String deviceId;
    Map<String, List<CmdRecord>> typeRecord = new HashMap<>();
    int cmdCount = 0;
    int ackCount = 0;

    public DeviceCmd(String deviceId) {
        this.deviceId = deviceId;
    }

    public void addRecord(String type, CmdRecord record) {
        if (!typeRecord.containsKey(type)) {
            typeRecord.put(type, new LinkedList<>());
        }
        typeRecord.get(type).add(record);
    }

    public void addCmd() {
        ++cmdCount;
    }
    public void addBoth() {
        addCmd();
        ++ackCount;
    }

    @Override
    public String toString() {
        return "DeviceCmd{" +
                "deviceId='" + deviceId + '\'' +
                ", typeRecord=" + typeRecord +
                ", cmdCount=" + cmdCount +
                ", ackCount=" + ackCount +
                '}' + '\n';
    }
}
