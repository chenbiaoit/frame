package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepositiry extends JpaRepository<SysOperationLog,Integer> {
}
