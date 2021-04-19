package cn.cyberpeace.mp.base.repository;

import java.util.List;

public interface BaseModelMapper<Model> {
    List<Model> list(Integer limit, Integer offset, List<String> selectFields);
    Integer total();
    Model get(String id, List<String> selectFields);
    Boolean create(Model obj, List<String> createFields);
    Boolean update(String id, Model obj, List<String> updateFields);
    Boolean delete(String id);
}
