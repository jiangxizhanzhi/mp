 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ImageTargetConvertModel.java.
 // File size 522.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ImageTargetConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ImageTargetConvertModel{

    /**
     * UUID
     */
    private String uuid;

    /**
     * 名称
     */
    private String name;

}
