 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CloudDeleteResponse.java.
 // File size 435.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.model;

import lombok.Data;

/**
 * @ClassName CloudDeleteResponse
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
public class CloudDeleteResponse extends BaseModel{
    private Boolean success;
    private String message;

    public boolean successFlag() {
        return Boolean.TRUE.equals(this.getSuccess());
    }
}
