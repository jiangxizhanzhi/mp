 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowVoConverter.java.
 // File size 772.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.vo.convert;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.OpenFlow;
import cn.cyberpeace.mp.devopscluster.vo.OpenFlowVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * @ClassName OpenFlowVoConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see IConverter
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface OpenFlowVoConverter extends IConverter<OpenFlow, OpenFlowVO>{

    @Mapping(target = "openFlowMatchConditionVO",ignore = true)
    @Override
    OpenFlowVO sourceToTarget(OpenFlow openFlow);
}
