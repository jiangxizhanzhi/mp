package cn.cyberpeace.mp.aggregation.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @DefaultStr
    private String operationType;
    @DefaultStr
    private String currentUrl;
    @DefaultStr
    private String title;
    @DefaultStr
    private String parameter;
    @DefaultStr
    private String userId;
    @DefaultStr
    private String userName;



}
