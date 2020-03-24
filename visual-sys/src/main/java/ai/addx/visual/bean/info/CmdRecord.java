package ai.addx.visual.bean.info;

import lombok.Data;

@Data
public class CmdRecord {
    String id;
    String deviceId;
    String cmdTime;
    String ackTime;
    String type;
    boolean ack = false;

    public CmdRecord(String id, String deviceId, String sendTime, String type) {
        this.id = id;
        this.deviceId = deviceId;
        this.cmdTime = sendTime;
        this.type = type;
    }

    public void setAckTime(String ackTime) {
        this.ackTime = ackTime;
        this.ack = true;
    }

    @Override
    public String toString() {
        return "CmdRecord{" +
                "id='" + id + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", cmdTime='" + cmdTime + '\'' +
                ", ackTime='" + ackTime + '\'' +
                ", type='" + type + '\'' +
                ", ack=" + ack +
                '}' + '\n';
    }
}
