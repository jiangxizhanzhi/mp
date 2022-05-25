package cn.cyberpeace.mp.auth.dto.third;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @Author qiaokang
 * @Date：2022/5/24 18:01
 */
@Data
public class ThirdUser implements Serializable {

    private String userId;

    private String userName;

    private String createTime;

    private String orgId;

    private String chineseName;

    private Integer useFlag;

    private String phone;

}
