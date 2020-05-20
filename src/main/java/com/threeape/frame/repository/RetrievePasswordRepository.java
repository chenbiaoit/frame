package com.threeape.frame.repository;

import com.threeape.frame.entity.system.SysRetrievePassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetrievePasswordRepository extends JpaRepository<SysRetrievePassword,Integer> {

    SysRetrievePassword findByUseridAndRandomCode(Integer userId,String randomCode);
}
