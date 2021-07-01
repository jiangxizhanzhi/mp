package cn.cyberpeace.mp.auth.dto.org;

import lombok.Data;

@Data
public class OrgUserPageRequest {

    private String username;

    private String org;

    private Integer page;

    private Integer pageSize;

}
