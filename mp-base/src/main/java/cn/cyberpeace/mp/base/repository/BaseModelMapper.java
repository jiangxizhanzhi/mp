package cn.cyberpeace.mp.base.repository;

import java.util.List;

public interface BaseModelMapper<Model> {
    List<Model> queryList(Integer offset, Integer limit);
    Model get(String id);
    Boolean create(Model model);
    Boolean update(Model model);
    Boolean delete(String id);
}
