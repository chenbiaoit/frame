package com.threeape.frame.entity.system;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity(name = "t_sys_permission")
public class SysPermission {

    @Id
    @GeneratedValue
    private Integer permissionId;

    private Short active;

    private String iconClass;

    private Integer parentId;

    private String permissDesc;

    private String name;

    private Integer orderValue;

    private Integer type;

    private String url;

    private String prefixUrl;

    @JSONField(serialize=false)
    @CreatedDate
    private Date createTime;

    @JSONField(serialize=false)
    private Integer createUserId;

    @JSONField(serialize=false)
    @LastModifiedDate
    private Date updateTime;

    @JSONField(serialize=false)
    private Integer updateUserId;

    //菜单相关角色
    @ManyToMany(mappedBy="sysPermissions",fetch = FetchType.LAZY)
    @JSONField(serialize=false)
    private List<SysRole> roles;


    //Ext
    @Transient
    private List<SysPermission> children;

    @Override
    public String toString() {
        return "SysPermission{" +
                "permissionId=" + permissionId +
                ", active=" + active +
                ", iconClass='" + iconClass + '\'' +
                ", parentId=" + parentId +
                ", desc='" + permissDesc + '\'' +
                ", name='" + name + '\'' +
                ", order=" + orderValue +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", prefixUrl='" + prefixUrl + '\'' +
                ", createTime=" + createTime +
                ", createUserId=" + createUserId +
                ", updateTime=" + updateTime +
                ", updateUserId=" + updateUserId +
                ", children=" + children +
                '}';
    }
}