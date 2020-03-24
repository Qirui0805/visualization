package ai.addx.visual.bean.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectStatusDTO {
//    @JsonProperty("name")
    String time;
    String message;
//    @JsonProperty("value")
    int status;
    public ConnectStatusDTO(String time, String message, int status) {
        this.time = time;
        this.message = message;
        this.status = status;
    }
}
