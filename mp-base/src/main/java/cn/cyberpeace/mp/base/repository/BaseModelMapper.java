package cn.cyberpeace.mp.base.repository;

import java.util.List;

public interface BaseModelMapper<Model> {
    List<Model> list(Integer limit, Integer offset, String[] selectFields);
    Integer total();
    Model get(String id, String[] selectFields);
    Boolean create(Model obj, String[] createFields, Object[] createFieldValues);
    Boolean update(String id, Model obj, String[] updateFields, Object[] updateFieldValues);
    Boolean delete(String id);
}
