package cn.cyberpeace.mp.auth.request.user;

import lombok.Data;

@Data
public class ThirdUserRequest {
    private String id;
    private String username;
    private String name;
    private String org;
    private String role;
}
