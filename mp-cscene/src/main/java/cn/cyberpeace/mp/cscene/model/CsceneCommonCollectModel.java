package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName CsceneCommonCollectModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@Table("mp_cscene_common_collect")
public class CsceneCommonCollectModel extends BaseModel {

	private String csceneId;

	private String nodeId;

	private String hostName;

	private String agreement;

	private LocalDateTime time;

	private String detail;
}
