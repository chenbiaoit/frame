package com.threeape.frame.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 22:44 2019/4/8
 * @Modified by:
 */
public class Enums {

    /**
     * 审批状态
     * pending:待审批
     * pass:通过
     * reject:驳回
     */
    public enum APPROVAL_STATUS {
        pending,pass,reject;

        public static boolean isExists(String approvalStatus){
            for(Enum status : APPROVAL_STATUS.values()){
                if(String.valueOf(status).equals(approvalStatus)){
                    return true;
                }
            }
            return false;
        }
    }

    public enum YES_NO {
        YES(1),
        NO(0);

        private int code;

        YES_NO(Integer code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }


    /**
     * 角色类型
     */
    public enum ROLE_TYPE {
        USER(1),
        SUB_USER(2);

        private int roleType;

        ROLE_TYPE(Integer roleType){
            this.roleType = roleType;
        }

        public int getRoleType() {
            return roleType;
        }
    }

    /**
     * 用户类型
     * agent.代理商
     * subAgent.子代理商
     * internal.内部用户(展锐)
     */
    public enum USER_TYPE{
        agent,subAgent,internal,sales
    }

    /**
     * 用户状态：正常、冻结
     */
    public enum USER_STATUS{
        normal(1),freeze(0);

        private final int code;

        USER_STATUS(int code){
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 资源类型: 菜单、按钮、接口api
     */
    public enum PERMISSION_TYPE_ENUM {
        MENU(1),BUTTON(2),API(3);

        private final int type;

        PERMISSION_TYPE_ENUM(int type){
            this.type = type;
        }
        public int getType() {
            return type;
        }

        public static List<Integer> getResourceTypes(){
            List<Integer> types = new ArrayList<>();
            for(PERMISSION_TYPE_ENUM type : PERMISSION_TYPE_ENUM.values()){
                types.add(type.getType());
            }
            return types;
        }
    }
}
