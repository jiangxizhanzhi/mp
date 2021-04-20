package cn.cyberpeace.mp.base.repository;

import java.util.List;

public interface BaseModelMapper<Model> {
    List<Model> list(Integer limit, Integer offset, String[] selectFields);
    Integer total();
    Model get(String id, String[] selectFields);
    Boolean create(String[] createFields, Object[] createFieldValues);
    Boolean update(String id, String[] updateFields, Object[] updateFieldValues);
    Boolean delete(String id);
    Integer bulkCreate(String[] createFields, Object[][] createFieldValuesList);
    Integer bulkUpdate(String[] updateFields, Object[][] createFieldValuesList);
    Integer bulkDelete(String[] ids);
}
