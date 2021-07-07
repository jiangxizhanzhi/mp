package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(TargetModel.class)
public interface TargetMapper extends BaseModelMapper<TargetModel> {

    @Select("select * from mp_auth_target where tenant = #{tenantId}")
    List<TargetModel> listByTenantId(@Param("tenantId") String tenantId);


    @Select("select * from mp_auth_target where parent = #{parentId}")
    List<TargetModel> listByParent(@Param("parentId") String parentId);

    @Select("<script>" +
            "SELECT * FROM mp_auth_target " +
            "<where>" +
            "  <if test=\"type != null\">" +
            "    AND type = #{type}" +
            "  </if>" +
            "  <if test=\"tenantId != null and tenantId != ''\">" +
            "    AND tenant = #{tenantId}" +
            "  </if>" +
            "</where></script>")
    List<TargetModel> listAll(@Param("tenantId") String tenantId, @Param("type") Integer type);


    @Select("<script>" +
            "SELECT * FROM mp_auth_target " +
            "<where>" +
            "  <if test=\"types != null\"> " +
            "    AND type IN " +
            "    <foreach item=\"type\" index=\"index\" collection=\"types\" open=\"(\" separator=\",\" close=\")\"> " +
            "      #{type} " +
            "    </foreach>" +
            "  </if>" +
            "  <if test=\"tenantId != null and tenantId != ''\">" +
            "    AND tenant = #{tenantId}" +
            "  </if>" +
            "</where></script>")
    List<TargetModel> listByMultiTypes(@Param("tenantId") String tenantId, @Param("types") Integer[] types);
}
