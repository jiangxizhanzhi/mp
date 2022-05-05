package cn.cyberpeace.mp.situationaware.service;

import cn.cyberpeace.mp.situationaware.repository.ProbeMapper;
import cn.cyberpeace.mp.situationaware.repository.ProbeParamMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
  *@Desciption
  *@Author zhangjin
  *@Date 9/10/21 11:12 上午
**/
//@Service
@Slf4j
public class ProbeParamService {

    @Autowired
    private ProbeMapper probeMapper;

    @Autowired
    private ProbeParamMapper probeParamMapper;



}
