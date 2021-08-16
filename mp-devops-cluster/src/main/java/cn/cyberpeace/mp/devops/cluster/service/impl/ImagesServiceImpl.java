package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.devops.cluster.config.DevopsConfig;
import cn.cyberpeace.mp.devops.cluster.service.ImagesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ImagesServiceImpl extends BaseOpenStackServiceImpl implements ImagesService {

    public ImagesServiceImpl(DevopsConfig devopsConfig, ObjectMapper objectMapper) {
        super(devopsConfig, objectMapper);
    }

    @Override
    public void page(String getParams) {

    }

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }

    @Override
    public void build() {

    }

    @Override
    public void delete() {

    }

    @Override
    public void auth() {

    }

    @Override
    public void export() {

    }
}
