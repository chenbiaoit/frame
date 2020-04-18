package com.threeape.frame.entity.system;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

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
    private Date createTime;

    @JSONField(serialize=false)
    private Integer createUserId;

    private String roleCode;

    private String roleName;

    private Integer roleType;

    @JSONField(serialize=false)
    private Date updateTime;

    @JSONField(serialize=false)
    private Integer updateUserId;

    @JSONField(serialize=false)
    private Short active;

    //角色相关用户
    @OneToMany(cascade=CascadeType.REFRESH,mappedBy="sysrole",fetch = FetchType.LAZY)
    private List<SysUser> users;

    //角色相关权限
    @ManyToMany(cascade=CascadeType.REFRESH)
    @JoinTable(name="t_sys_role_permission",
            inverseJoinColumns=@JoinColumn(name="permission_id"),
            joinColumns=@JoinColumn(name="role_id"))
    private List<SysPermission> sysPermissions;
}