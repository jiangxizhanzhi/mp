 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SuricataRuleMapper.java.
 // File size 673.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.situationaware.model.SuricataRuleModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.repository
 * @Author: hhuang
 * @CreateTime: 2023/4/21 17:11
 * @Description: Suricata规则数据访问
 * @Version: 1.0
 */
@Mapper
@Repository
@ModelClass(SuricataRuleModel.class)
public interface SuricataRuleMapper extends BaseModelMapper<SuricataRuleModel> {
}
