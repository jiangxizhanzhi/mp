 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivMenuDTO.java.
 // File size 689.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ActivMenuDTO
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
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
