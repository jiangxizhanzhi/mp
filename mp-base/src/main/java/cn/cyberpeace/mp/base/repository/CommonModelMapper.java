package cn.cyberpeace.mp.base.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CommonModelMapper {
    Boolean create(String table, String[] createFields, Object[] createFieldValues);
    Boolean update(String table, String id, String[] updateFields, Object[] updateFieldValues);
    Boolean delete(String table, String id);
    Integer bulkCreate(String table, String[] createFields, Object[][] createFieldValuesList);
    Integer bulkUpdate(String table, String[] updateFields, Object[][] updateFieldValuesList);
    Integer bulkDelete(String table, String[] ids);
}
