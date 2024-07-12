 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowPcapMapper.java.
 // File size 621.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

import cn.cyberpeace.mp.flow.model.FlowPcapModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName FlowPcapMapper
 * @Description 流量包mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:21:37
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowPcapModel.class)
public interface FlowPcapMapper extends BaseModelMapper<FlowPcapModel> {
	
}
