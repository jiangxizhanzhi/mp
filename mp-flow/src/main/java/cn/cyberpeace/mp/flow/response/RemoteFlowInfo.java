// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name BackgroundFlowResponse.java.
// File size 2245.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BackgroundFlowResponse
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see FlowInfoResponse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoteFlowInfo {
    private String flowId;
    private String title;
    private String file;
    private String fileName;
    private String pcapFile;
    private Integer interactiveType;
    private List<ProtocolBO> protocolList;
    private List<ScriptParamModel> scriptParamModels;

}
