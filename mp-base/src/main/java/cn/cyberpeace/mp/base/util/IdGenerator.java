package cn.cyberpeace.mp.base.util;

import java.util.UUID;

/**
 * ID 生成器
 * @author zhangchengbin
 */
public class IdGenerator {

    private final static SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(25, 26);

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

    public static void main(String[] args){
        for (int i=0;i<10;i++){
            System.out.println(generateGlobalId());
        }
    }
}
