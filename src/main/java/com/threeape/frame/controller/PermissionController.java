package com.threeape.frame.controller;

import com.threeape.frame.bean.system.PermissionBean;
import com.threeape.frame.entity.system.SysPermission;
import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.service.PermissionService;
import com.threeape.frame.service.RoleService;
import com.threeape.frame.service.UserService;
import com.threeape.frame.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 23:37 2019/6/10
 * @Modified by:
 */
@RestController
@Slf4j
@RequestMapping("/permission")
public class PermissionController extends BaseController{

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;


    /**
     * 获取当前登录用户下拥有的所有资源
     * @return
     */
    @GetMapping(value="/getAll")
    public BaseResponse getAll(){
        Map<String,Object> root = new HashMap<>();
        root.put("id",0);
        root.put("resourceName","菜单");
        root.put("children",Collections.EMPTY_LIST);
        SysRole currentRole = super.getCurrentRole();
        Integer roleID = null;
        List<SysPermission> permissions = null;
        if(!currentRole.getRoleCode().equals("ADMIN")){
            SysRole currRole = roleService.findRoleById(currentRole.getRoleId());
            permissions = currentRole.getSysPermissions();
        }else{
            permissions = permissionService.findAll();
        }
        if(!permissions.isEmpty()){
            //获取树形结构
            List<SysPermission> resources = permissionService.convertPermissionTree(permissions);
            root.put("children",resources);
        }
        return super.successResult(root);
    }

    /**
     * 获取角色对应的资源id
     * @param roleCode
     * @return
     */
    @GetMapping(value = "/findPermission/{roleCode}")
    public BaseResponse findPermission(@PathVariable String roleCode) {
        BusinessUtil.notNull(roleCode, ErrorCodes.SystemManagerEnum.ROLE_EMPTY_CODE);
        SysRole role = roleService.findRoleByCode(roleCode);
        BusinessUtil.notNull(role, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);

        List<Integer> permissionIds = new ArrayList<>();
        List<SysPermission> sysPermissions = role.getSysPermissions();
        if(!CollectionUtils.isEmpty(sysPermissions)){
            permissionIds = sysPermissions
                                    .stream().map(x -> x.getPermissionId())
                                    .collect(Collectors.toList());
        }
        return super.successResult(permissionIds);
    }

    /**
     * 给角色赋资源权限
     * @return
     */
    @PostMapping(value = "/savePermission")
    public BaseResponse empowerment(@RequestBody PermissionBean permissionBean) {
        BusinessUtil.notNull(permissionBean.getRoleCode(), ErrorCodes.SystemManagerEnum.ROLE_EMPTY_CODE);
        List<Integer> permissionIds = permissionBean.getPermissionIds();
        permissionService.savePermission(permissionIds,permissionBean.getRoleCode(),super.getCurrentUserId());
        return super.successResult();
    }

    /**
     * 给指定用户赋角色
     * @param loginName
     * @param roleCode
     * @return
     */
    @PostMapping(value = "/improveUserPerm")
    public BaseResponse improveUserPerm(@RequestParam String loginName,
                                        @RequestParam String roleCode){

        SysUser user = userService.findUser(loginName);
        BusinessUtil.notNull(user, ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS);
        SysRole role = roleService.findRoleByCode(roleCode);
        BusinessUtil.notNull(role, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);
        permissionService.improveUserPerm(role.getRoleId(),user.getUserId(),super.getCurrentUser().getUserId());
        return super.successResult();
    }

    /**
     * 添加资源信息
     * @return
     */
    @PostMapping(value="/addResource")
    public BaseResponse addResource(@RequestBody SysPermission permission){
        if(permission.getParentId() == null
                || StringUtils.isEmpty(permission.getName())
                || StringUtils.isEmpty(permission.getUrl())
                || permission.getType() == null){

            throw new BusinessException(ErrorCodes.SystemManagerEnum.RESOURCE_ILLEGAL.getCode(),
                    ErrorCodes.SystemManagerEnum.RESOURCE_ILLEGAL.getZhMsg());
        }
        //如果资源类型不在枚举中定义
        if(!Enums.PERMISSION_TYPE_ENUM.getResourceTypes().contains(permission.getType())){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.RESOURCE_TYPE_NOT_EXIST.getCode(),
                    ErrorCodes.SystemManagerEnum.RESOURCE_TYPE_NOT_EXIST.getZhMsg());
        }
        if(permissionService.findById(permission.getParentId()) == null && 0 != permission.getParentId()){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.RESOURCE_PARENT_NOT_EXIST.getCode(),
                    ErrorCodes.SystemManagerEnum.RESOURCE_PARENT_NOT_EXIST.getZhMsg());
        }
        if(permissionService.findByName(permission.getName()) != null){
            throw new BusinessException(ErrorCodes.SystemManagerEnum.RESOURCE_EXIST.getCode(),
                    ErrorCodes.SystemManagerEnum.RESOURCE_EXIST.getZhMsg());
        }
        permissionService.savePermission(permission,super.getCurrentUser().getUserId());
        return super.successResult();
    }

    /**
     * 准备数据
     * @return
     */
    @GetMapping(value="/findRes/{permissionId}")
    public BaseResponse preEdit(@PathVariable Integer resourceId){
        SysPermission permission = permissionService.findById(resourceId);
        BusinessUtil.notNull(permission, ErrorCodes.SystemManagerEnum.RESOURCE_NOT_EXIST);
        return super.successResult(permission);
    }

    /**
     * 修改资源信息
     * @return
     */
    @PostMapping(value="/editResource")
    public BaseResponse editResource(@RequestBody SysPermission permission){
        if(permission.getType() == null
                || StringUtils.isEmpty(permission.getName())
                || StringUtils.isEmpty(permission.getUrl())
                || permission.getPermissionId() == null){

            throw new BusinessException(ErrorCodes.SystemManagerEnum.RESOURCE_ILLEGAL.getCode(),
                    ErrorCodes.SystemManagerEnum.RESOURCE_ILLEGAL.getZhMsg());
        }

        if(!Enums.PERMISSION_TYPE_ENUM.getResourceTypes().contains(permission.getType())){
           throw new BusinessException(ErrorCodes.SystemManagerEnum.RESOURCE_TYPE_NOT_EXIST.getCode(),
                    ErrorCodes.SystemManagerEnum.RESOURCE_TYPE_NOT_EXIST.getZhMsg());
        }

        SysPermission res = permissionService.findById(permission.getPermissionId());
        BusinessUtil.notNull(res, ErrorCodes.SystemManagerEnum.RESOURCE_NOT_EXIST);

        if(permission.getParentId() != 0){
            SysPermission parentPermission = permissionService.findById(permission.getParentId());
            BusinessUtil.notNull(parentPermission, ErrorCodes.SystemManagerEnum.RESOURCE_PARENT_NOT_EXIST);
        }
        permissionService.savePermission(permission,super.getCurrentUserId());
        return super.successResult();
    }

    /**
     * 删除资源信息
     * @return
     */
    @DeleteMapping(value="/delResource/{permissionId}")
    public BaseResponse deleteResource(@PathVariable Integer permissionId){

        SysPermission permission = permissionService.findById(permissionId);
        BusinessUtil.notNull(permission, ErrorCodes.SystemManagerEnum.RESOURCE_NOT_EXIST);

        List<SysPermission> resourceList = permissionService.findByParentId(permissionId);
        BusinessUtil.assertTrue(resourceList.isEmpty(), ErrorCodes.SystemManagerEnum.RESOURCE_HAS_CHILDREN);


        int result = permission.getRoles().size();
        BusinessUtil.assertFlase(result > 0, ErrorCodes.SystemManagerEnum.RESOURCE_USED);

        permissionService.deletePermission(permissionId);
        return super.successResult();
    }
}
