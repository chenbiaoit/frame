package com.threeape.frame.entity.system;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "t_sys_resource")
public class SysPermission {

    @Id
    @GeneratedValue
    private Integer permissionId;

    private Short active;

    private String iconClass;

    private Integer parentId;

    private String resourceDesc;

    private String resourceName;

    private Integer resourceOrder;

    private Integer resourceType;

    private String resourceUrl;

    private String permissionPrefixUrl;

    @JSONField(serialize=false)
    private Date createTime;
    @JSONField(serialize=false)
    private Integer createUserId;
    @JSONField(serialize=false)
    private Date updateTime;
    @JSONField(serialize=false)
    private Integer updateUserId;

    //菜单相关角色
    @ManyToMany(cascade= CascadeType.REFRESH,mappedBy="sysPermissions",fetch = FetchType.LAZY)
    private List<SysRole> roles;


    //Ext
    @Transient
    private List<SysPermission> children;
}