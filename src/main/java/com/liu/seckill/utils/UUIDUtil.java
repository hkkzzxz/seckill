package com.liu.seckill.utils;

import java.util.UUID;


public class UUIDUtil {
//建立当前账号uuid防止数据库
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
