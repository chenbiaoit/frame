package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<SysUser,Integer>, JpaSpecificationExecutor {

    SysUser findByLoginName(String loginName);

}
