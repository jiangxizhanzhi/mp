 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneAttCkTypeCountInfo.java.
 // File size 516.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.sceneplugincollection.bo.AttCkTypeCount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName SceneAttCkTypeCountInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SceneAttCkTypeCountInfo {
    private Long totalTimes;
    private List<AttCkTypeCount> list;
}
