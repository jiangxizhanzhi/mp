 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EsCollectionDataStorageModel.java.
 // File size 1029.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName EsCollectionDataStorageModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_es_collection_data_storage")
public class EsCollectionDataStorageModel extends BaseModel {

	/**
	 * 日期
	 */
	@ApiModelProperty(value = "日期")
	@DefaultStr
	private String date;

	/**
	 * 存储通道
	 */
	@ApiModelProperty(value = "存储通道")
	@DefaultStr
	private String channel;

	/**
	 * 状态
	 */
	@ApiModelProperty(value = "状态")
	@DefaultStr
	private String status;


}
