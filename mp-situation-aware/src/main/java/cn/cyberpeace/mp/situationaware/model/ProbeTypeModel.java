package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.common.model.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/26
 */
@Data
@Builder
@NoArgsConstructor
@Table("mp_situation_probe_type")
public class ProbeTypeModel extends ApiModel {
}

