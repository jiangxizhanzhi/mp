 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RandomUtils.java.
 // File size 1010.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @ClassName RandomUtils
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public class RandomUtils {

    // 字符集合
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CHARACTERS_LENGTH = CHARACTERS.length();
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();


    /**
     * 获取随机字母和数字组合
     * @param length
     * @return
     */
    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(CHARACTERS_LENGTH);
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
