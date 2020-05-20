package com.threeape.frame.entity.system;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * 
 * @author Bill
 * @date   2019-09-09 22:55::17
 */
@Data
@Entity(name = "t_sys_retrieve_password")
public class SysRetrievePassword {

    @Id
    @GeneratedValue
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userid;

    /**
     * 随机数
     */
    private String randomCode;

    /**
     * 失效时间
     */
    private Date invalidTime;

    /**
     * 1.生效 0失效
     */
    private Integer status;

    /**
     * 创建时间
     */
    @CreatedDate
    private Date createTime;

    /**
     * 修改时间
     */
    @LastModifiedDate
    private Date updateTime;

}