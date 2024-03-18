package cn.cyberpeace.mp.scene.model;


import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cyber_range_register")
public class CyberRangeRegisterModel extends BaseModel {

    /**
     * 靶场唯一标识
     */
    @DefaultStr
    private String rangeTag;

    @DefaultStr
    private String rangeIp;

    @DefaultStr
    private String rangePort;

    @DefaultInt(RangeStatus.OFFLINE)
    private Integer rangeStatus;



    public static class RangeStatus {
        public static final int OFFLINE = 0;
        public static final int ONLINE = 1;

        private RangeStatus() {}
    }
}
