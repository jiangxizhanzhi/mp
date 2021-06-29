package cn.cyberpeace.mp.auth.dto.excel;

import lombok.Data;

import java.util.Date;

@Data
public class ImportUser {

    private String username;
    private String name;
    private String password;
    private String orgpath;
    private String roles;

}
