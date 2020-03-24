package ai.addx.visual.bean.info;

import lombok.Data;

@Data
public class LiveResponse {
    String id;
    String serialNumber;
    String responseTime;

    public LiveResponse(String id, String serialNumber, String responseTime) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.responseTime = responseTime;
    }
}
