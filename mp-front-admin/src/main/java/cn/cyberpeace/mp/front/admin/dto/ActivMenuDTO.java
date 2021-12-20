package cn.cyberpeace.mp.front.admin.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivMenuDTO {

    private String id;

    private String name;

    private String value;

    private String authority;

    private String path_name;

    private int status;

    private int read_status;

    private int write_status;

    private int execute_status;

    private String per_status;

    private List<String> active;

    private List<String> edition;

    List<ActivMenuDTO> children;

}
