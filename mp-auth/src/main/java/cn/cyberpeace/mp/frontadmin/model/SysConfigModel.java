package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sys_config")
public class SysConfigModel extends BaseModel {
    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "配置名")
    private String keyName;

    @ApiModelProperty(value = "配置值")
    private String value;

    @ApiModelProperty(value = "初始值")
    private String initValue;



    public static class Type {
        public static final String TEXT = "text";
        public static final String NUMBER = "number";
        public static final String SELECT_ONE = "selectOne";
        public static final String SELECT_MULTI = "selectMulti";
    }

    public static class KeyName {

        public static final String FLOW_COMPRESSION__PASSWORD = "flow_compression_password";
        public static final String FLOW_COMPRESSION_TYPE = "flow_compression_type";
        public static final String FLOW_PERSISTENCE_TIMESTAMP_TYPE = "flow_persistence_timestamp_type";
        public static final String FLOW_PERSISTENCE_TIME_RANGE = "flow_persistence_time_range";

    }

    public static class COMPRESSION_TYPE {
        public static final String ZIP = "ZIP";
        public static final String LZ4 = "LZ4";
    }

    public static class TIMESTAMP_TYPE {
        public static final String NTP = "ntp";
        public static final String PTP = "ptp";
    }



}
