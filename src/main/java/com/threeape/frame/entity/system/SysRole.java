package com.threeape.frame.entity.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.Proxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "t_sys_role")
public class SysRole {

    @Id
    @GeneratedValue
    private Integer roleId;

    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;

    @JSONField(serialize=false)
    private Integer createUserId;

    private String roleCode;

    private String roleName;

    private Integer roleType;

    @JSONField(serialize=false)
    @LastModifiedDate
    private Date updateTime;

    @JSONField(serialize=false)
    private Integer updateUserId;

    @JSONField(serialize=false)
    private Short active;

    //角色相关用户
    @OneToMany(mappedBy= "sysRole",fetch = FetchType.LAZY)
    @JSONField(serialize=false)
    private List<SysUser> users;

    //角色相关权限
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="t_sys_role_permission",
            inverseJoinColumns=@JoinColumn(name="permission_id"),joinColumns=@JoinColumn(name="role_id"))
    @JSONField(serialize=false)
    private List<SysPermission> sysPermissions;

    @Override
    public String toString() {
        return "SysRole{" +
                "roleId=" + roleId +
                ", createTime=" + createTime +
                ", createUserId=" + createUserId +
                ", roleCode='" + roleCode + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleType=" + roleType +
                ", updateTime=" + updateTime +
                ", updateUserId=" + updateUserId +
                ", active=" + active +
                '}';
    }
}