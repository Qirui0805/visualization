package ai.addx.visual.bean.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BatteryDTO {
    String time;
    String battery;
    @JsonProperty("incident")
    Incident incident;
    public BatteryDTO(String time, String battery) {
        this.time = time;
        this.battery = battery;
    }

}
