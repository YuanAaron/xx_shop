package cn.coderap.entity;

import cn.coderap.constant.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Result<T> {
    private boolean flag; // 是否成功
    private Integer code; // 返回码
    private String message; // 返回消息
    private T data; // 返回数据

    public Result() {
        this.flag = true;
        this.code = StatusCode.OK;
        this.message = "执行成功";
    }

    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }
}
