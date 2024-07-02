package cn.cyberpeace.mp.aggregation.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName EventTrackingRecord
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@NoArgsConstructor
@Table("mp_event_tracking_record")
public class EventTrackingRecord extends BaseModel {
    @DefaultStr
    private String module;
    @DefaultStr
    private String category;
    @DefaultStr
    private String action;
    @DefaultStr
    private String target;
}
