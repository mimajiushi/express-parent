package com.cwj.express.common.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author cwj
 */
@Data
@ToString
@NoArgsConstructor
public class ResponseResult implements Response {

    /**
     * 操作是否成功
     */
    boolean success = SUCCESS;

    /**
     * 操作代码
     */
    int code = SUCCESS_CODE;


    /**
     * 返回数据
     */
    Object data;

    public ResponseResult(ResultCode resultCode){
        this.success = resultCode.success();
        this.code = resultCode.code();
        this.data = resultCode.message();
    }

    public static ResponseResult SUCCESS(){
        return new ResponseResult(CommonCode.SUCCESS);
    }
    public static ResponseResult FAIL(){
        return new ResponseResult(CommonCode.FAIL);
    }

    public static ResponseResult FAIL(ResultCode resultCode){
        return new ResponseResult(resultCode);
    }

    public static ResponseResult FAIL(Object data){
        ResponseResult res = new ResponseResult();
        res.setData(data);
        res.setSuccess(false);
        res.setCode(-999);
        return res;
    }

    public static ResponseResult SUCCESS(Object data){
        ResponseResult res = new ResponseResult();
        res.setData(data);
        res.setSuccess(true);
        res.setCode(SUCCESS_CODE);
        return res;
    }
}
