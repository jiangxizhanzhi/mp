package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrgMapper extends BaseModelMapper<OrgModel> {
}
