package com.threeape.frame.controller;

import com.threeape.frame.config.security.JwtUser;
import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.RoleRepository;
import com.threeape.frame.util.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 12:52 2019/5/3
 * @Modified by:t
 */
@Component
@Slf4j
public class BaseController {

    @Resource
    private RoleRepository roleRepository;

    /**
     * 获取当前登录用户
     * @return
     */
    protected SysUser getCurrentUser(){
        SysUser user = null;
        try {
            user = ((JwtUser) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal())
                    .getUser();
        } catch (Exception e) {
            log.error("Current user is null");
        }
        return user;
    }

    /**
     * 获取当前登录用户ID
     * @return
     */
    protected Integer getCurrentUserId(){
        return this.getCurrentUser().getUserId();
    }

    /**
     * 获取当前登录用户角色
     * @return
     */
    protected SysRole getCurrentRole(){
        Authentication authentication= SecurityContextHolder
                .getContext()
                .getAuthentication();

        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        List<String> list = grantedAuthorities.stream()
                .map(x->x.getAuthority()).collect(Collectors.toList());

        //目前只支持用户单角色
        String roleCode = list.get(0);
        return roleRepository.findByRoleCode(roleCode);
    }

    /**
     * 获取参数校验失败信息
     * @param errorList
     * @return
     */
    public String getValidExceptionMsg(List<ObjectError> errorList) {
        String errMsg =  errorList.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(","));
        if(errMsg.contains("crAmount")){
            return "CR金额请输入有效的数字";
        }
        return errMsg;
    }


    protected BaseResponse successResult() {
        BaseResponse response = new BaseResponse();
        response.success();
        return response;
    }

    protected BaseResponse successResult(Object data) {
        BaseResponse response = new BaseResponse();
        response.success(data);
        return response;
    }

    protected BaseResponse failResult(String message){
        BaseResponse response = new BaseResponse();
        response.fail(message);
        return response;
    }

}
