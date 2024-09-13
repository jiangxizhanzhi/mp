package cn.cyberpeace.mp.devopscluster.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.po.VulnTaskTargetPO;

import java.util.List;

/**
 * 扫描任务节点关联镜像
 */
@Mapper
@Repository
@ModelClass(VulnTaskTargetPO.class)
public interface VulnTaskTargetMapper extends BaseModelMapper<VulnTaskTargetPO> {

    VulnTaskTargetPO findByTaskIdAndNode(@Param("recordId") String recordId, @Param("node") String node);

    void deleteByParam(@Param("id") String id, @Param("recordId") String recordId, @Param("node") String node);

    List<VulnTaskTargetPO> getAllTaskBind();

    List<VulnTaskTargetPO> getTaskBindByRecordId( @Param("recordId") String recordId);
}
