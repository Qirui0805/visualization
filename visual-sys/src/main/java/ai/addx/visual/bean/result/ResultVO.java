package ai.addx.visual.bean.result;

import lombok.Data;

@Data
public class ResultVO {
    int code;
    String message;
    Object data;
    public ResultVO(int code, String message) {
        this.code = code;
        this.message = message;
    }
    public ResultVO(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
