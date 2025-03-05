package cn.cyberpeace.mp.auth.bo.user;

import lombok.Data;

@Data
public class TeamInfoResult {
    private String teamId;
    private String teamName;
    private String teamDesc;
    private String teamLogo;
    private String teamTag;
}
