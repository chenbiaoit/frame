package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysRole;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<SysRole,Integer> {

    SysRole findByRoleCodeAndActive(String roleCode,int actice);
}
