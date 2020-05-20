package com.threeape.frame.entity.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name = "t_sys_user")
public class SysUser {

    @Id
    @GeneratedValue
    private Integer userId;

    private String email;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    private String loginName;

    @JSONField(serialize=false)
    private String loginPwd;

    private String mobile;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date pwdInvalidTime;

    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 注册时间
     */
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date regTime;

    /**
     * 用户状态
     * 正常:1 冻结:0
     */
    private Integer userStatus;

    /**
     * 用户类型
     * @link Enums.USER_TYPE
     */
    private String userType;

    /**
     * 是否有效
     * 正常:1 逻辑删除:0
     */
    private Short active;

    @JSONField(serialize=false)
    private Integer createUserId;

    @JSONField(serialize=false)
    @CreatedDate
    private Date createTime;

    @JSONField(serialize=false)
    private Integer updateUserId;

    @JSONField(serialize=false)
    @LastModifiedDate
    private Date updateTime;

    //用户角色
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(name="t_sys_role_user",
            inverseJoinColumns = @JoinColumn(name="role_id"),joinColumns = @JoinColumn(name="userId"))
    private SysRole sysRole;

    @JSONField(serialize=false)
    private Date regStartTime;
    @JSONField(serialize=false)
    private Date regEndTime;

    @Override
    public String toString() {
        return "SysUser{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", loginName='" + loginName + '\'' +
                ", loginPwd='" + loginPwd + '\'' +
                ", mobile='" + mobile + '\'' +
                ", pwdInvalidTime=" + pwdInvalidTime +
                ", customerName='" + customerName + '\'' +
                ", regTime=" + regTime +
                ", userStatus=" + userStatus +
                ", userType='" + userType + '\'' +
                ", active=" + active +
                ", createUserId=" + createUserId +
                ", createTime=" + createTime +
                ", updateUserId=" + updateUserId +
                ", updateTime=" + updateTime +
                ", regStartTime=" + regStartTime +
                ", regEndTime=" + regEndTime +
                '}';
    }
}