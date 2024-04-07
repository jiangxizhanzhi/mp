package cn.cyberpeace.mp.auth.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ThirdUserRequest {
    @NotBlank
    private String id;
    @NotBlank
    private String username;
    @NotBlank
    private String name;
    @NotBlank
    private String org;
    @NotBlank
    @Pattern(regexp = "(学员)|(管理员)")
    private String role;
}
