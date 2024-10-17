package cn.cyberpeace.mp.auth.request.team;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRequest {


    private String id;
    //战队名称
    private String name;
    //logo
    private String logo;
    //标签
    private String tag;
    //描述
    private String description;
    //人员数量
    private Integer userCount;
    //创建者
    private String creator;
}
