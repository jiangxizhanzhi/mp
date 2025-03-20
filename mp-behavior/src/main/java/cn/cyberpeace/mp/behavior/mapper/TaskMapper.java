package cn.cyberpeace.mp.behavior.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.TaskModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(TaskModel.class)
public interface TaskMapper extends BaseModelMapper<TaskModel> {

	Integer countTask();
}
