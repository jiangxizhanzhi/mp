 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowProtocolMapper.java.
 // File size 644.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.repository;

 import cn.cyberpeace.mp.base.repository.BaseModelMapper;
 import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
 import cn.cyberpeace.mp.flow.model.FlowProtocolModel;
 import org.apache.ibatis.annotations.Mapper;
 import org.springframework.stereotype.Repository;

 import java.util.List;

 /**
 * @ClassName FlowProtocolMapper
 * @Description 流量协议mapper
 * @Author qiaokang
 * @Date 2023-04-27 09:22:12
 * @Version 1.0.0
 * @see BaseModelMapper
 */
@Mapper
@Repository
@ModelClass(FlowProtocolModel.class)
public interface FlowProtocolMapper extends BaseModelMapper<FlowProtocolModel> {


    List<String> listProtocols();
}
