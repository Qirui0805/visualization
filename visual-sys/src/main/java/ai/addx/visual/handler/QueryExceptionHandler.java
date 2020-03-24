package ai.addx.visual.handler;

import ai.addx.visual.exception.QueryException;
import ai.addx.visual.bean.result.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class QueryExceptionHandler {

    @ExceptionHandler(value = QueryException.class)
    @ResponseBody
    public ResultVO handleQueryException(QueryException e) {
        return new ResultVO(1, "error", e.getMessage());
    }
}
