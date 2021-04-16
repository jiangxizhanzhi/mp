package cn.cyberpeace.mp.base.service;

import java.util.ArrayList;
import java.util.List;

public interface BaseModelService<Model> {

    default List<Model> queryList(Integer page, Integer pageSize) {
        return new ArrayList<>();
    }

    default Model get(String id) {
        return null;
    }

    default Model create(Model model) {
        return null;
    }

    default Model update(Model model) {
        return null;
    }

    default Boolean delete(String id) {
        return false;
    }
}
