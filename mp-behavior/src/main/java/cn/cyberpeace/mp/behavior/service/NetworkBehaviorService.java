package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.behavior.mapper.NetworkBehaviorMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NetworkBehaviorService {

	@Autowired
	private NetworkBehaviorMapper networkBehaviorMapper;


}
