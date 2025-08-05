package com.example.loginservice.util;

public class OptUtil {

    //生成指定位數的opt
    public static String generateOpt(int length) {
        StringBuilder opt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = (int) (Math.random() * 10); // 生成0-9的随机数字
            opt.append(digit);
        }
        return opt.toString();
    }

}
