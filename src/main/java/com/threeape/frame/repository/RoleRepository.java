package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysRole;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<SysRole,Integer>, JpaSpecificationExecutor {

    SysRole findByRoleCode(String roleCode);

    SysRole findByRoleName(String roleName);
}
