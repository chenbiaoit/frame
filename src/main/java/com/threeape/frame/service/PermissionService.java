package com.threeape.frame.service;

import com.threeape.frame.entity.system.SysPermission;
import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.PermissionRepository;
import com.threeape.frame.repository.RoleRepository;
import com.threeape.frame.repository.UserRepository;
import com.threeape.frame.util.BusinessUtil;
import com.threeape.frame.util.DateUtil;
import com.threeape.frame.util.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PermissionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;

    /**
     * 获取用户所有可访问的资源,可缓存至redis
     * @param userName
     * @return
     */
    public List<SysPermission> findUserPerMission(String userName){
        if(StringUtils.isEmpty(userName)){
            return null;
        }
        SysUser user = userRepository.findByLoginName(userName);
        Assert.notNull(user,"current user is null");

        SysRole role = user.getSysRole();
        if(Objects.isNull(role)){
            log.warn("用户没有分配角色");
            return null;
        }
        return role.getSysPermissions();
    }

    /**
     * 分配权限
     * @param permissionIds 最新的权限列表
     * @param userId
     * @return
     */
    public void savePermission(List<Integer> permissionIds,String roleCode,Integer userId){
        SysRole roleDO = roleRepository.findByRoleCode(roleCode);
        BusinessUtil.notNull(roleDO, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);
        this.setPermission(permissionIds, roleDO.getRoleId(),userId);
    }

    /**
     * 指定用户赋权
     * @param roleId
     * @param userId
     * @param currentUserId
     */
    public void improveUserPerm(Integer roleId,Integer userId,Integer currentUserId){
        Optional<SysUser> userOptional = userRepository.findById(userId);
        BusinessUtil.notNull(userOptional, ErrorCodes.SystemManagerEnum.USER_NOT_EXISTS);
        SysUser sysUser = userOptional.get();

        Optional<SysRole> roleOptional = roleRepository.findById(roleId);
        BusinessUtil.notNull(userOptional, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);
        SysRole sysRole = roleOptional.get();

        sysUser.setSysRole(sysRole);
        sysUser.setUpdateUserId(userId);
        userRepository.save(sysUser);
    }

    /**
     * 对角色进行权限设置
     * @param permissionIds 权限列表
     * @param roleId 角色id
     * @param userId 操作人
     * @return
     */
    private void setPermission(List<Integer> permissionIds, Integer roleId, Integer userId) {

        Optional<SysRole> roleOptional = roleRepository.findById(roleId);
        BusinessUtil.notNull(roleOptional, ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);

        SysRole sysRole = roleOptional.get();

        //check
        this.checkParams(permissionIds);

        List<SysPermission> permissions = permissionIds.stream().map(x -> {
            Optional<SysPermission> permissionOptional = permissionRepository.findById(x);
            BusinessUtil.notNull(permissionOptional, ErrorCodes.SystemManagerEnum.RESOURCE_NOT_EXIST);

            return permissionOptional.get();
        }).collect(Collectors.toList());

        sysRole.setSysPermissions(permissions);
        sysRole.setUpdateUserId(userId);
        roleRepository.save(sysRole);
    }

    private void checkParams(List<Integer> resourcesIds) {
        List<Integer> errorList = resourcesIds.stream().filter(x->
                x == null
        ).collect(Collectors.toList());
        //不能存在空值数据
        BusinessUtil.isEmptyList(errorList, ErrorCodes.SystemManagerEnum.RESOURCE_NOT_EXIST);
    }

    /**
     * 添加/修改资源
     *
     * @param permission
     * @return
     */
    public void savePermission(SysPermission permission, Integer userId){
        int result;
        if(permission.getPermissionId() == null) {
            //菜单顺序暂时不做维护
            int maxOrder = permissionRepository.maxOrder();
            permission.setOrderValue(maxOrder + 1);
            permission.setActive((short)1);
            permission.setCreateUserId(userId);
        }else{
            permission.setUpdateUserId(userId);
        }
        permissionRepository.save(permission);
    }

    /**
     * 删除权限
     * @param permissionId
     */
    public void deletePermission(Integer permissionId){
        permissionRepository.deleteById(permissionId);
    }


    /**
     * 获取所有权限
     * @return
     */
    public List<SysPermission> findAll(){
       return permissionRepository.findAll();
    }

    /**
     * 根据id查抄权限信息
     * @param id
     * @return
     */
    public SysPermission findById(Integer id){
        Optional<SysPermission> permissionOptional = permissionRepository.findById(id);
        return permissionOptional == null ? null : permissionOptional.get();
    }

    /**
     * 获取当前父节点下面的权限资源
     * @param parentId
     * @return
     */
    public List<SysPermission> findByParentId(Integer parentId){
        return permissionRepository.findByParentId(parentId);
    }

    /**
     * 根据权限名称查找
     * @param name
     * @return
     */
    public SysPermission findByName(String name){
        return permissionRepository.findByName(name);
    }


    /**
     * 将list转为tree结构
     * @param menuList
     * @return
     */
    public List<SysPermission> convertPermissionTree(List<SysPermission> menuList){
        List<SysPermission> menuResources = new ArrayList<>();
        for (SysPermission permission : menuList) {
            if (permission.getParentId().equals(0) && permission.getType().equals(1)) {
                SysPermission crmResourceDO = this.deepFindPermission(permission,menuList);
                menuResources.add(crmResourceDO);
            }
        }
        return menuResources;
    }

    private SysPermission deepFindPermission(SysPermission sysPermission, List<SysPermission> menuList){
        for(SysPermission currResource : menuList){
            if(currResource.getParentId().equals(sysPermission.getPermissionId())
                    &&  currResource.getType().equals(1)){

                List<SysPermission> childrenResource = sysPermission.getChildren();
                childrenResource.add(currResource);
                sysPermission.setChildren(childrenResource);
                //递归寻找
                this.deepFindPermission(currResource,menuList);
            }
        }
        return sysPermission;
    }
}
