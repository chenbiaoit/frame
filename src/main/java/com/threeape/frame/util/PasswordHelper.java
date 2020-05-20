package com.threeape.frame.util;

public class PasswordHelper {

    /**
     * 重置密码随机生成10位密码数
     * @return
     */
    public static String generateRandomPassword() {
        int length = 10;
        char[] ss = new char[10];
        int[] flag = { 0, 0, 0 }; // A-Z, a-z, 0-9
        int i = 0;
        while (flag[0] == 0 || flag[1] == 0 || flag[2] == 0 || i < length) {
            i = i % length;
            int f = (int) (Math.random() * 3 % 3);
            if (f == 0)
                ss[i] = (char) ('A' + Math.random() * 26);
            else if (f == 1)
                ss[i] = (char) ('a' + Math.random() * 26);
            else
                ss[i] = (char) ('0' + Math.random() * 10);
            flag[f] = 1;
            i++;
        }
        return new String(ss) + "$";
    }

}
