package cn.cyberpeace.mp.auth.bo.excel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportUserBo {

    
    private String username;

    
    private String name;

    
    private String password;

    
    private String orgpath;

    
    private String roles;

    private String expiredTime;

    private String description;

    
    private List<String> roleIdList;

    
    private List<String> orgIdList;

}
