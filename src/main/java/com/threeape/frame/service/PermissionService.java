package com.threeape.frame.service;

import com.threeape.frame.entity.system.SysPermission;
import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class PermissionService {

    @Resource
    private UserRepository userRepository;

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

        SysRole role = user.getSysrole();
        if(Objects.isNull(role)){
            log.warn("用户没有分配角色");
            return null;
        }
        return role.getSysPermissions();
    }

    /**
     * 将list转为tree结构
     * @param menuList
     * @return
     */
    public List<SysPermission> convertPermissionTree(List<SysPermission> menuList){
        List<SysPermission> menuResources = new ArrayList<>();
        for (SysPermission tCrmResourceDO : menuList) {
            if (tCrmResourceDO.getParentId().equals(0) && tCrmResourceDO.getResourceType().equals(1)) {
                SysPermission crmResourceDO = this.deepFindResouce(tCrmResourceDO,menuList);
                menuResources.add(crmResourceDO);
            }
        }
        return menuResources;
    }

    private SysPermission deepFindResouce(SysPermission sysPermission,List<SysPermission> menuList){
        for(SysPermission currResource : menuList){
            if(currResource.getParentId().equals(sysPermission.getPermissionId())
                    &&  currResource.getResourceType().equals(1)){

                List<SysPermission> childrenResource = sysPermission.getChildren();
                childrenResource.add(currResource);
                sysPermission.setChildren(childrenResource);
                //递归寻找
                this.deepFindResouce(currResource,menuList);
            }
        }
        return sysPermission;
    }
}
