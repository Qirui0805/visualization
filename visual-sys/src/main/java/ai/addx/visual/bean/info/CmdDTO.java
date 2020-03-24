package ai.addx.visual.bean.info;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CmdDTO {
    int cmdCount = 0;
    int ackCount = 0;
    List<DeviceCmd> deviceCmds = new ArrayList<>();

    public void addDeviceCmd(DeviceCmd cmd) {
        deviceCmds.add(cmd);
        cmdCount += cmd.getCmdCount();
        ackCount += cmd.getAckCount();
    }

    @Override
    public String toString() {
        return "CmdDTO{" +
                "cmdCount=" + cmdCount +
                ", ackCount=" + ackCount +
                ", deviceCmds=" + deviceCmds +
                '}' + '\n';
    }
}
