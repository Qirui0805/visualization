package ai.addx.visual.bean.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class APIDTO {
    @JsonProperty("name")
    String api;
    @JsonProperty("value")
    int count;
    public APIDTO(String api, int count) {
        this.api = api;
        this.count = count;
    }
}
