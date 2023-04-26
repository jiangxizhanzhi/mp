package cn.cyberpeace.mp.sceneplugincollection.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.sceneplugincollection.model.SuricataRuleModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/*
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincollection.repository
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
