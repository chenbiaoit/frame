package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<SysPermission,Integer> {

    SysPermission findByName(String name);

    List<SysPermission> findByParentId(Integer parentId);

    @Query(value = "select count(1) from SysPermission",nativeQuery = true)
    int maxOrder();
}
