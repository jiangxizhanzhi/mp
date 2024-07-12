 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EventModel.java.
 // File size 828.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.event.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EventModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventModel extends BaseModel {
    @DefaultStr
    private String target;
    @DefaultInt
    private Integer event;
    @DefaultInt
    private Integer eventStatus;
    @DefaultInt
    private String process;
    @DefaultInt
    private Integer processStatus;
    @DefaultStr
    private String processMessage;
}
