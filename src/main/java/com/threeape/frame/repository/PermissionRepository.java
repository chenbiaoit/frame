package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<SysPermission,Integer> {
}
