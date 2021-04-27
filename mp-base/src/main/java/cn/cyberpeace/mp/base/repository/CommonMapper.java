package cn.cyberpeace.mp.base.repository;

import cn.cyberpeace.mp.base.repository.condition.Condition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CommonMapper {
    List<Map<String, Object>> list(String table, @Param("condition") Condition condition, String[] selectFields);
    Integer total(String table, @Param("condition") Condition condition);
    Map<String, Object> get(String table, String id, String[] selectFields);
    Boolean create(String table, String[] createFields, Object[] createFieldValues);
    Boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);
    Boolean delete(String table, String id);
    Integer bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);
    Integer bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);
    Integer bulkDelete(String table, String[] ids);
}
