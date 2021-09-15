package cn.cyberpeace.mp.auth.test;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/14 15:36
 */
@Data
public class TestVerifyRequest {

    private String verifyStr;

    private String verifyCode;

}
