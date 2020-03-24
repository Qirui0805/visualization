package ai.addx.visual.bean.info;

import lombok.Data;

@Data
public class LiveRecord {
    String id;
    String serialNumber;
    String requestTime;
    String responseTime;
    //0 stands for success while 1 means failure
    int status = -1;

    public LiveRecord(String id, String serialNumber, String requestTime) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.requestTime = requestTime;
    }

    public LiveRecord(String id, String serialNumber, String requestTime, String responseTime, int status) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.requestTime = requestTime;
        this.responseTime = responseTime;
        this.status = status;
    }
}
