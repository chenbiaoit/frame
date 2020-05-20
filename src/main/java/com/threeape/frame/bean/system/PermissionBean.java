package com.threeape.frame.bean.system;

import lombok.Data;

import java.util.List;

@Data
public class PermissionBean {
    //角色编码
    private String roleCode;

    private List<Integer> permissionIds;
}
