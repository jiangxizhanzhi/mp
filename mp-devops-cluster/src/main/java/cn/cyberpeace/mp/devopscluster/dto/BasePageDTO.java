 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BasePageDTO.java.
 // File size 629.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BasePageDTO
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseDTO
 */
@Data
@NoArgsConstructor
public class BasePageDTO extends BaseDTO{

    /**
     * 每页条数
     */
    private Integer limit;

    /**
     * 页码
     */
    private Integer offset;

    private Integer page;

    private Integer perPage;


    public BasePageDTO(Integer limit, Integer offset) {
        this.limit = limit;
        this.offset = offset;
    }
}
