package com.threeape.frame.util;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 17:15 2019/6/8
 * @Modified by:
 */
public class ErrorCodes {

    public enum CommonEnum implements ErrorInfo {
        SYSTEM_EXCEPTION(-1,"系统异常","system error"),
        SYSTEM_TIMEOUT(0,"系统超时","system timeout"),
        SUCCESS(1,"操作成功","operate successfully"),
        REQ_PARAM_FORMAT_ERROR(-2, "请求参数格式异常", ""),
        REQ_ILLEGAL(-3, "非法请求", "");

        private final int code;
        private final String zhMsg;
        private final String enMsg;

        CommonEnum(int code, String zhMsg, String enMsg) {
            this.code = code;
            this.zhMsg = zhMsg;
            this.enMsg = enMsg;
        }

        public int getCode() {
            return code;
        }

        public String getZhMsg() {
            return zhMsg;
        }

        public String getEnMsg() {
            return enMsg;
        }
    }

    /**
     * 系统管理状态码
     */
    public enum SystemManagerEnum implements ErrorInfo {
        ACCOUNT_ERROR(10001,"登录名或密码错误",""),
        LOCKED(10002,"账户锁定",""),
        PASSWORD_INVALID(10003,"密码过期,请联系管理员重置",""),
        AUTH_ERROR(10004,"权限不足",""),
        TOKEN_INVALID(10005,"会话已经过期,请重新登录",""),
        USER_EXISTS(10010,"登录名已经存在",""),
        USER_EMPTY_USER_NAME(10011,"登录名不能为空",""),
        USER_NOT_EXISTS(10012,"用户不存在",""),
        USER_INVALID_PASSWORD(10013,"原密码不正确",""),
        USER_SAVE_FAILED(10014,"用户保存失败",""),
        USER_FOGET_EMAIL_URL_INVALID(10015,"链接已经失效",""),
        USER_EMAIL_INVALID(10016,"用户邮箱不合法",""),
        ROLE_NAME_EXISTS(10020,"角色名已经存在",""),
        ROLE_NOT_EXIST(10021,"角色不存在",""),
        ROLE_EMPTY_NAME(10022,"角色名不能为空",""),
        ROLE_EMPTY_CODE(10023,"角色编码不能为空",""),
        ROLE_EMPTY_ID(10024,"角色ID不能为空",""),
        ROLE_SAVE_FAILED(10025,"角色保存失败",""),
        ROLE_CODE_EXISTS(10026,"角色编码已经存在",""),
        ROLE_EMPTY_TYPE(10027,"角色类型不能为空",""),
        ROLE_BIND_USER(10028,"该角色已经绑定在用户上,不允许删除",""),
        ROLE_BIND_RES(10028,"该角色已经绑定在资源上,不允许删除",""),
        RESOURCE_ILLEGAL(10040,"资源必填项不能为空",""),
        RESOURCE_NOT_EXIST(10041,"资源不存在",""),
        RESOURCE_PARENT_NOT_EXIST(10042,"父级资源不存在",""),
        RESOURCE_HAS_CHILDREN(10044,"该资源下面有子资源,不允许删除",""),
        RESOURCE_USED(10045,"该资源已经绑定在角色上，不允许删除",""),
        RESOURCE_TYPE_NOT_EXIST(10046,"资源类型不存在",""),
        RESOURCE_EXIST(10047,"资源名称已经存在",""),

        SYS_PARAM_VALUE(10048,"该选项下Code已存在",""),
        SYS_BEAN_ERROR(10049,"对象映射异常",""),
        SYS_IN_USER_ERROR(10050,"员工信息缺失，请联系管理员维护",""),

        VERIFY_CODE_ERROR(10051, "验证码错误", "")
        ;



        private final int code;
        private final String zhMsg;
        private final String enMsg;

        SystemManagerEnum(int code, String zhMsg, String enMsg) {
            this.code = code;
            this.zhMsg = zhMsg;
            this.enMsg = enMsg;
        }

        public static String getZhMsgByCode(int code){
            for(SystemManagerEnum systemManagerEnum : SystemManagerEnum.values()){
                if(systemManagerEnum.code == code){
                    return systemManagerEnum.zhMsg;
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }

        public String getZhMsg() {
            return zhMsg;
        }

        public String getEnMsg() {
            return enMsg;
        }
    }
}
