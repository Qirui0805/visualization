package ai.addx.visual.bean.info;

import lombok.Data;

import java.util.Map;

@Data
public class Incident {
    String time;
    String message;
    int por;
    int awakenCount;
    int videoCount;
    Map<Integer, Integer> porCount;

}
