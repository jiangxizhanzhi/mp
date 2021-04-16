package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class BaseModelServiceImpl<Model extends BaseModel, ModelMapper extends BaseModelMapper<Model>> implements BaseModelService<Model> {

    private ModelMapper modelMapper;

    @Override
    public List<Model> queryList(Integer page, Integer pageSize) {
        return new ArrayList<>(modelMapper.queryList(page - 1, pageSize));
    }

    @Override
    public Model get(String id) {
        return modelMapper.get(id);
    }

    @Override
    public Model create(Model obj) {
        obj.initDefault();
        modelMapper.create(obj);
        return obj;
    }

    @Override
    public Model update(Model obj) {
        obj.initDefaultUpdateTime();
        modelMapper.update(obj);
        return obj;
    }

    @Override
    public Boolean delete(String id) {
        return modelMapper.delete(id);
    }
}
