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
    int total(String table, @Param("condition") Condition condition);
    boolean exist(String table, @Param("condition") Condition condition);
    Map<String, Object> get(String table, String id, String[] selectFields);
    boolean create(String table, String[] createFields, Object[] createFieldValues);
    boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);
    boolean delete(String table, String id);
    int bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);
    int bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);
    int bulkDelete(String table, String[] ids);
}
