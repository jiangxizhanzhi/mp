package cn.cyberpeace.mp.auth.extensions.security.xss;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/13 16:24
 */
@Data
public class XssBlackListItem {

    private String urlpattern;

    private String fields;

    private String methods;

}
