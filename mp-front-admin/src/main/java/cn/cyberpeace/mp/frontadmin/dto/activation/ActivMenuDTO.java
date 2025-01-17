 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ActivMenuDTO.java.
 // File size 645.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:14 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.dto.activation;

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
