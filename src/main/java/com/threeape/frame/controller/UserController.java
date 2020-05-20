package com.threeape.frame.controller;

import com.github.pagehelper.PageInfo;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.service.UserService;
import com.threeape.frame.util.BaseResponse;
import com.threeape.frame.util.BusinessUtil;
import com.threeape.frame.util.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    /**
     * 分页查询
     * @return
     */
    @PostMapping("/list")
    public BaseResponse selectAllUsers(@RequestBody(required = false) SysUser user,
                                       @RequestParam Integer pageNum,
                                       @RequestParam Integer pageSize){

        List<SysUser> users = userService.findWithPage(user,pageNum,pageSize);
        return super.successResult(users);
    }

    /**
     * 用户详情
     * @param loginName
     * @return
     */
    @GetMapping("/find/{loginName}")
    public BaseResponse findUser(@PathVariable String loginName){
        return super.successResult(userService.findUser(loginName));
    }

    /**
     * 登录名称是否可用
     * @return
     */
    @GetMapping("/validLoginName/{loginName}")
    public BaseResponse checkLoginName(@PathVariable String loginName) {
        SysUser user = userService.findUser(loginName);
        Map<String,Boolean> map = this.valid( user == null );
        return super.successResult(map);
    }

    /**
     * 修改密码
     * @throws Exception
     */
    @PostMapping("/modifyPwd")
    public BaseResponse modifyPwd(@RequestParam String loginName,
                                  @RequestParam String oldPwd,
                                  @RequestParam String newPwd) {

        //重置密码
        userService.modifyPwd(loginName,oldPwd,newPwd,super.getCurrentUser().getUserId());
        return super.successResult();
    }

    /**
     * 忘记密码-发送邮件
     * @param loginName
     * @return
     */
    @PostMapping("/forgetPwd/sendEmail/{loginName}")
    public BaseResponse sendEmail(@PathVariable String loginName){
        SysUser user = userService.findUser(loginName);
        BusinessUtil.notNull(user, ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS);

        userService.sendForgetEmail(loginName);
        return super.successResult();
    }

    /**
     * 忘记密码-修改密码
     * @param loginName
     * @param sid
     * @param newPwd
     * @return
     */
    @PostMapping("/forgetPwd/modifyPwd/{loginName}")
    public BaseResponse forgetPwd(@PathVariable String loginName,
                                  @RequestParam String sid,
                                  @RequestParam String newPwd){

        userService.modifyPwd(loginName,sid,newPwd);
        return successResult();
    }

    /**
     * 密码重置
     * @throws Exception
     */
    @PostMapping("/resetPwd/{loginName}")
    public BaseResponse resetPwd(@PathVariable String loginName) {
        SysUser currentUser = super.getCurrentUser();
        log.info("管理员{} 进行重置 '{}' 用户的密码操作",currentUser.getLoginName(),loginName);
        //重置密码
        userService.resetUserPwd(loginName,currentUser);
        return super.successResult();
    }

    /**
     * 修改用户生命周期
     * @return
     */
    @PostMapping("/modifyLifecycle/{loginName}/{userStatus}")
    public BaseResponse modifyLifecycle(@PathVariable String loginName,@PathVariable Integer userStatus){
        userService.modifyLifecycle(loginName,userStatus,super.getCurrentUserId());
        return super.successResult();
    }

    private Map<String, Boolean> valid(Boolean valid) {
        Map<String, Boolean> map = new HashMap<>();
        map.put("valid", valid);
        return map;
    }
}
