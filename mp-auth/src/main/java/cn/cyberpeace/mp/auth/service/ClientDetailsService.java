 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientDetailsService.java.
 // File size 622.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

/**
 * @ClassName ClientDetailsService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface ClientDetailsService extends BaseModelService<ClientDetailsModel, QueryParams> {

    /**
     * getByClientId
     *
     * @param clientId
     * @return
     */
    ClientDetailsModel getByClientId(String clientId);

}
