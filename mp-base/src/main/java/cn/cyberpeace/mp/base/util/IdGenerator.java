package cn.cyberpeace.mp.base.util;

import java.util.Random;
import java.util.UUID;

/**
 * ID 生成器
 * @author zhangchengbin
 */
public class IdGenerator {

    private final static SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(25, 26);

    // 含有数字
    private static final String CONTAIN_DIGIT_REGEX = ".*[0-9].*";
    // 包含字母
    private static final String CONTAIN_LETTER_REGEX = ".*[a-zA-z].*";

    /**
     * 用雪花算法生成分布式ID
     * @return
     */
    public static long generateGlobalId(){
       return snowflakeIdWorker.nextId();
    }

    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 产生随机字母数字组合
     * @param length
     * @return
     */
    public static String generateRandomStrNum(int length) {

        String val = "";
        Random random = new Random();

        //参数length，表示生成几位随机数
        for(int i = 0; i < length; i++) {

            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if( "char".equalsIgnoreCase(charOrNum) ) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char)(random.nextInt(26) + temp);
            } else if( "num".equalsIgnoreCase(charOrNum) ) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        //生成的随机数如果不包含数字和字母，重新生成
        if(!val.matches(CONTAIN_DIGIT_REGEX)||!val.matches(CONTAIN_LETTER_REGEX)){
            return generateRandomStrNum(length);
        }
        return val;
    }


    public static void main(String[] args){
        for (int i=0;i<10;i++){
            System.out.println(generateGlobalId());
        }
    }
}
