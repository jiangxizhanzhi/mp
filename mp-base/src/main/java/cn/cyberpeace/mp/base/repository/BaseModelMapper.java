package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.repository.condition.Condition;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseModelMapper<Model> {
    List<Model> list(@Param("condition") Condition condition, String[] selectFields);
    Integer total(@Param("condition") Condition condition);
    Model get(String id, String[] selectFields);
    Boolean create(String[] createFields, Object[] createFieldValues);
    Boolean update(String id, String[] updateFields, Object[] updateFieldValues);
    Boolean delete(String id);
    Integer bulkCreate(String[] createFields, Object[][] createFieldValuesList);
    Integer bulkUpdate(String[] updateFields, Object[][] updateFieldValuesList);
    Integer bulkDelete(String[] ids);
}
