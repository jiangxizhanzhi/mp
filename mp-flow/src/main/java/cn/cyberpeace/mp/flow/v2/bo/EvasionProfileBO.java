package cn.cyberpeace.mp.flow.v2.bo;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/11/8
 */
@Data
public class EvasionProfileBO {
    private List<Profile> evasionProfiles;

    @Data
    public static class Profile {
        private String name;
        private String value;
    }
}
