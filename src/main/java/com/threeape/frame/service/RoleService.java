package com.threeape.frame.service;

import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.RoleRepository;
import com.threeape.frame.util.BeanUtils;
import com.threeape.frame.util.BusinessException;
import com.threeape.frame.util.BusinessUtil;
import com.threeape.frame.util.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoleService {

    @Resource
    private RoleRepository roleRepository;

    /**
     * 角色列表分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public List<SysRole> findWithPage(String roleCode, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Specification<SysUser> specification = (Specification<SysUser>) (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (!StringUtils.isEmpty(roleCode)) {
                // 此处为查询serverName中含有key的数据
                list.add(criteriaBuilder.like(root.get("roleCode"), "%" + roleCode + "%"));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return roleRepository.findAll(specification,pageable).getContent();
    }

    /**
     * 新增/更新 角色
     * @param roleVo
     * @return
     */
    public void saveRole(SysRole roleVo){
        try {
            if(roleVo.getRoleId() == null){
                roleRepository.save(roleVo);
            }else{
                Optional<SysRole> roleOptional = roleRepository.findById(roleVo.getRoleId());
                if(roleOptional != null){
                    SysRole sysRole = roleOptional.get();
                    BeanUtils.copyNotNullFields(roleVo,sysRole);
                    roleRepository.save(sysRole);
                }
            }
        } catch (Exception e) {
            log.error("",e);
            throw new BusinessException(ErrorCodes.SystemManagerEnum.ROLE_SAVE_FAILED);
        }
    }

    /**
     * 删除角色
     * @param roleCode
     * @return
     */
    public void deleteRole(String roleCode){

        SysRole role = roleRepository.findByRoleCode(roleCode);
        BusinessUtil.notNull(role,ErrorCodes.SystemManagerEnum.ROLE_NOT_EXIST);

        Integer roleUserBinds = role.getUsers().size();;
        BusinessUtil.assertFlase(roleUserBinds > 0,ErrorCodes.SystemManagerEnum.ROLE_BIND_USER);

        Integer roleResBinds = role.getSysPermissions().size();
        BusinessUtil.assertFlase(roleResBinds > 0,ErrorCodes.SystemManagerEnum.ROLE_BIND_RES);

        roleRepository.delete(role);
    }

    /**
     * 获取所有角色
     * @return
     */
    public List<SysRole> findRoles(){
        return roleRepository.findAll();
    }

    /**
     * 根据code查找角色信息
     * @param roleCode
     * @return
     */
    public SysRole findRoleByCode(String roleCode){
        return roleRepository.findByRoleCode(roleCode);
    }

    /**
     * 根据角色名称查找角色信息
     * @param roleName
     * @return
     */
    public SysRole findRoleByName(String roleName){
        return roleRepository.findByRoleName(roleName);
    }

    /**
     * 根据id查询角色信息
     * @param roleId
     * @return
     */
    public SysRole findRoleById(Integer roleId){
        Optional<SysRole> roleOptional = roleRepository.findById(roleId);
        return roleOptional == null ? null : roleOptional.get();
    }
}
