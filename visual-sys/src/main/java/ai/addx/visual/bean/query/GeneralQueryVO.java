package ai.addx.visual.bean.query;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class GeneralQueryVO {
    String serialNumber;
    String from;
    String to;
    String environment;
    String country;
}
