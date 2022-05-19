package cn.cyberpeace.mp.front.business.dto;

import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/19 09:42
 */
@Data
public class ImportUrlAuth {

    private String menuName;

    private String menuId;

    private String menuDesc;

    private String url;

    private String method;

}
