package cn.cyberpeace.mp.devopscluster.repository;


import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.devopscluster.model.TaskManagementModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(TaskManagementModel.class)
public interface TaskManagementMapper extends BaseModelMapper<TaskManagementModel> {


}
