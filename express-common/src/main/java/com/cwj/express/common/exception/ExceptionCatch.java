package com.cwj.express.common.exception;

import com.cwj.express.common.constant.URLConstant;
import com.cwj.express.common.model.response.CommonCode;
import com.cwj.express.common.model.response.ResponseResult;
import com.cwj.express.common.model.response.ResultCode;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 控制器增强统一异常捕获类
 * @author Administrator
 * @version 1.0
 * @create 2018-09-14 17:32
 **/
@Slf4j
@ControllerAdvice
public class ExceptionCatch {


    /**
     * 定义map，配置异常类型所对应的错误代码
     */
    private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
    /**
     * 定义map的builder对象，去构建ImmutableMap
     */
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    /**
     * 捕获 CustomException
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException customException){
        customException.printStackTrace();
        ResultCode resultCode = customException.getResultCode();
        //记录日志
        log.error("catch exception:{}",resultCode.message());
        return new ResponseResult(resultCode);
    }

    /**
     * 捕获参数验证异常 MethodArgumentNotValidException
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseResult customException(MethodArgumentNotValidException validException){
        validException.printStackTrace();
        //记录日志
        log.error("catch exception:{}", validException.getMessage());
        return ResponseResult.FAIL(validException.getMessage());
    }


    /**
     * 捕获 AccessDeniedException 无权访问时的异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public void customException(AccessDeniedException accessDeniedException, HttpServletResponse response) throws IOException {
//        accessDeniedException.printStackTrace();
        //记录日志
        log.error("catch exception:{}",CommonCode.UNAUTHORISE.message());
        response.sendRedirect(URLConstant.LOGIN_PAGE_URL);
//        return new ResponseResult(CommonCode.UNAUTHORISE);
    }


    /**
     * 捕获 Exception(无法预料) 异常
     */
    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseResult exception(Exception exception){
        if (exception instanceof AccessDeniedException){

        }
        exception.printStackTrace();
        //记录日志
        log.error("catch exception:{}",exception.getMessage());
        if(EXCEPTIONS == null){
            //EXCEPTIONS构建成功
            EXCEPTIONS = builder.build();
        }
        //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
        ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
        if(resultCode !=null){
            return new ResponseResult(resultCode);
        }else{
            //返回99999异常
            return new ResponseResult(CommonCode.SERVER_ERROR);
        }


    }

    static {
        //定义异常类型所对应的错误代码
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }
}
