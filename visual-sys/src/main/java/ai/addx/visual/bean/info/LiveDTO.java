package ai.addx.visual.bean.info;

import lombok.Data;

import java.util.List;
@Data
public class LiveDTO {
    String serialNumber;
    List<LiveRecord> messages;
    int requestCount;
    int responseCount;

    public LiveDTO(String serialNumber, int requestCount, int responseCount) {
        this.serialNumber = serialNumber;
        this.requestCount = requestCount;
        this.responseCount = responseCount;
    }

    public LiveDTO(String serialNumber, List<LiveRecord> messages, int requestCount, int responseCount) {
        this.serialNumber = serialNumber;
        this.messages = messages;
        this.requestCount = requestCount;
        this.responseCount = responseCount;
    }
}
