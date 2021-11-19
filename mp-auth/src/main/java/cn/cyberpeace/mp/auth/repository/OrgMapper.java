package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(OrgModel.class)
public interface OrgMapper extends BaseModelMapper<OrgModel> {

    List<OrgModel> listParents();

    List<OrgModel> listByParentId(@Param("parentId") String parentId);

    List<OrgModel> listAll(@Param("tenantId") String tenantId);

    OrgModel getByPath(@Param("path") String path);

    int updatePath(@Param("id") String id, @Param("path") String path);
}
