package com.threeape.frame.controller;

import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.service.RoleService;
import com.threeape.frame.util.BaseResponse;
import com.threeape.frame.util.BusinessUtil;
import com.threeape.frame.util.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 22:49 2019/6/10
 * @Modified by:
 */
@RestController
@Slf4j
@RequestMapping("/permission")
public class RoleController extends BaseController {

    @Resource
    private RoleService roleService;

    /**
     * 获取当前页
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/rolePageInfo")
    public BaseResponse userSetting(@RequestParam(required = false) String roleCode,
                                    @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                    @RequestParam(required = false,defaultValue = "10") Integer pageSize) {

        List<SysRole> pager = roleService.findWithPage(roleCode, pageNum,pageSize);
        return super.successResult(pager);
    }

    @GetMapping(value = "/findRoles")
    public BaseResponse findRoles() {
        List<SysRole> roles = roleService.findRoles();
        return super.successResult(roles);
    }

    /**
     * 新增角色
     * @return
     */
    @PostMapping(value = "/saveRole")
    public BaseResponse saveRole(@RequestBody SysRole role) {
        BusinessUtil.assertEmpty(role.getRoleCode(), ErrorCodes.SystemManagerEnum.ROLE_EMPTY_CODE);
        BusinessUtil.assertEmpty(role.getRoleName(), ErrorCodes.SystemManagerEnum.ROLE_EMPTY_NAME);
        BusinessUtil.notNull(role.getRoleType(), ErrorCodes.SystemManagerEnum.ROLE_EMPTY_TYPE);

        SysRole roleCodeQuery = roleService.findRoleByCode(role.getRoleCode());
        BusinessUtil.isNull(roleCodeQuery, ErrorCodes.SystemManagerEnum.ROLE_CODE_EXISTS);

        SysRole roleNameQuery = roleService.findRoleByName(role.getRoleName());
        BusinessUtil.isNull(roleNameQuery, ErrorCodes.SystemManagerEnum.ROLE_NAME_EXISTS);

        role.setActive((short)1);
        role.setCreateUserId(super.getCurrentUser().getUserId());
        roleService.saveRole(role);
        return super.successResult();
    }

    /**
     * 查询角色详情
     * @return
     */
    @GetMapping(value = "/findRole/{roleCode}")
    public BaseResponse findRole(@PathVariable String roleCode) {
        SysRole role = roleService.findRoleByCode(roleCode);
        BusinessUtil.notNull(role, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);
        BusinessUtil.assertEmpty(role.getRoleName(), ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);
        return super.successResult(role);
    }

    /**
     * 修改角色
     * @return
     */
    @PostMapping(value = "/updateRole")
    public BaseResponse updateRole(@RequestBody SysRole role) {
        BusinessUtil.notNull(role.getRoleId(), ErrorCodes.SystemManagerEnum.ROLE_EMPTY_ID);
        BusinessUtil.assertEmpty(role.getRoleCode(), ErrorCodes.SystemManagerEnum.ROLE_EMPTY_CODE);

        SysRole currentRole = roleService.findRoleById(role.getRoleId());
        BusinessUtil.notNull(currentRole, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);

        String currentRoleName = currentRole.getRoleName();
        String currentRoleCode = currentRole.getRoleCode();

        SysRole checkRoleName = roleService.findRoleByName(role.getRoleName());
        if(checkRoleName != null && !currentRoleName.equals(checkRoleName.getRoleName())){
            BusinessUtil.assertFlase(true, ErrorCodes.SystemManagerEnum.ROLE_NAME_EXISTS);
        }

        SysRole checkRoleCode = roleService.findRoleByCode(role.getRoleCode());
        if(checkRoleCode != null && !currentRoleCode.equals(checkRoleCode.getRoleCode())){
            BusinessUtil.assertFlase(true, ErrorCodes.SystemManagerEnum.ROLE_CODE_EXISTS);
        }

        role.setUpdateUserId(super.getCurrentUser().getUserId());
        roleService.saveRole(role);
        return super.successResult();
    }

    /**
     * 删除角色
     * @param roleCode
     * @return
     */
    @DeleteMapping("/delete/{roleCode}")
    public BaseResponse delete(@PathVariable String roleCode){
        roleService.deleteRole(roleCode);
        return super.successResult();
    }
}
