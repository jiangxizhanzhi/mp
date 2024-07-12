 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OpenFlowPageConverterModel.java.
 // File size 651.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import cn.cyberpeace.mp.devopscluster.vo.OpenFlowVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName OpenFlowPageConverterModel
 * @Description 路由分页返回信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageConvertModel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenFlowPageConverterModel extends BasePageConvertModel {

    /**
     * 列表
     */
    private List<OpenFlowVO> openFlowVOS;

}
