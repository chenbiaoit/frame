package com.threeape.frame.config.aop;

import com.alibaba.fastjson.JSON;
import com.threeape.frame.config.security.JwtUser;
import com.threeape.frame.entity.system.SysOperationLog;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.OperationLogRepositiry;
import com.threeape.frame.util.BusinessException;
import com.threeape.frame.util.ErrorCodes;
import com.threeape.frame.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Desc: 操作日志
 * @Author: Bill
 * @Date: created in 11:36 2019-06-17
 * @Modified by:
 */
@Aspect
@Component
@Slf4j
public class OperationAspect{

    @Resource
    private OperationLogRepositiry operationLogRepositiry;


    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog){
        Object proceed = null;
        SysOperationLog opLog = new SysOperationLog();
        try {
            this.buildOperationLog(point,opLog);
        } catch (Exception e) {
            //构建操作日志对象出现异常不影响Controller继续执行
            log.error("Aop intercepts log exceptions.",e);
        }
        try {
            //前置增强结束,继续执行原有方法
            proceed = point.proceed();
        } catch (Throwable throwable) {
            //设置错误信息并且抛出相应异常
            this.setErrorMsgAndThrowException(throwable,opLog);
        }finally {
            //后置增强，保存日志
            this.saveLog(opLog);
        }
        return proceed;
    }

    /**
     * 设置错误信息并且抛出相应异常
     * @param opLog
     * @param throwable
     */
    private void setErrorMsgAndThrowException(Throwable throwable,SysOperationLog opLog) {
        if(throwable instanceof BusinessException){
            BusinessException ex = (BusinessException)throwable;
            opLog.setErrorMsg(ex.getMessage());
            throw ex;
        }
        if(throwable instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) throwable;
            List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
            String msg = allErrors.stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
            opLog.setErrorMsg(msg);
            throw new BusinessException(ErrorCodes.CommonEnum.REQ_PARAM_FORMAT_ERROR.getCode(),msg);
        }
        if(throwable instanceof BindException){
            String msg = ((BindException) throwable).getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
            opLog.setErrorMsg(msg);
            throw new BusinessException(ErrorCodes.CommonEnum.REQ_PARAM_FORMAT_ERROR.getCode(),msg);
        }
        opLog.setErrorMsg(ExceptionUtils.getExceptionAllinformation(throwable));
        throw new RuntimeException("Unknown Exception",throwable);
    }

    /**
     * 组装操作日志对象
     * @param point
     * @return
     */
    private void buildOperationLog(ProceedingJoinPoint point, SysOperationLog opLog) {
        //获取操作人
        SysUser user = ((JwtUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUser();

        opLog.setOperator(Objects.isNull(user)? null: user.getLoginName());

        //获取请求URL
        HttpServletRequest request = this.getRequest();
        Assert.notNull(request,"requestAttributes is null");
        opLog.setUrl(request.getRequestURI());

        Object[] objects = point.getArgs();
        //获取方法参数
        String params = this.getParams(objects);
        //获取调用链
        Signature signature = point.getSignature();
        String invoke = this.getInvoke(params, point.getSignature());
        opLog.setInvoke(invoke);

        //获取业务日志Key(类名.方法名)
        String className = point.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        opLog.setBusinessKey(String.format("%s.%s",className,methodName));
    }

    /**
     * 获取方法参数
     * @param objects
     * @return
     */
    private String getParams(Object[] objects) {
        String params = null;
        if(Objects.nonNull(objects)){
            params = Stream.of(objects).map(x->{
                Object obj = x;
                try {
                    //如果是上传文件,普通序列化会报错,需要做一层处理
                    if(x instanceof MultipartFile[]){
                        MultipartFile[] multipartFiles = (MultipartFile[])x;
                        obj = Stream.of(multipartFiles)
                                .collect(Collectors.toMap(
                                        MultipartFile::getName,
                                        MultipartFile::getOriginalFilename));

                    }
                    if(x instanceof MultipartFile){
                        MultipartFile multipartFile = (MultipartFile)x;
                        return multipartFile.getName();
                    }
                    return JSON.toJSONString(obj);
                } catch (Exception e) {
                    log.error("",e);
                    return null;
                }
            }).collect(Collectors.joining(","));
        }
        return params;
    }

    /**
     * 获取容器中的Request
     * @return
     */
    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.notNull(requestAttributes,"requestAttributes is null");
        return (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
    }

    /**
     * 获取调用方法&参数
     * @param params
     * @param signature
     * @return
     */
    private String getInvoke(String params, Signature signature) {
        String declaringTypeName = signature.getDeclaringTypeName();
        String className = signature.getName();

        StringBuilder sb = new StringBuilder(declaringTypeName)
                .append(".")
                .append(className);

        if(Objects.nonNull(params)){
            sb.append(" -> parameters：");
            sb.append(params);
        }
        return sb.toString();
    }

    /**
     * 持久化到DB,暂存mysql,当操作日志较多时,可以考虑日志归档或者ES
     * @param operationLog
     */
    private void saveLog(SysOperationLog operationLog){
        operationLogRepositiry.save(operationLog);
    }
}
