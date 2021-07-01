package cn.cyberpeace.mp.auth.util;

import lombok.Data;

import java.util.List;

@Data
public class TmpMenuDTO {

    private String name;

    private String value;

    private String authority;

    private String path_name;

    private int status;

    private int read_status;

    private int write_status;

    private int execute_status;

    private int per_status;

    List<TmpMenuDTO> children;

}
