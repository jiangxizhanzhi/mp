package cn.cyberpeace.mp.scene.controller;

import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.vo.SceneConfigVO;
import cn.cyberpeace.mp.scene.websocket.SceneConfigWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class SceneConfigController {

    @Autowired
    private SceneConfigService sceneConfigService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SceneConfigWebSocket sceneConfigWebSocket;

    @GetMapping("/scene")
    public List<SceneConfigVO> findAll() {
        return sceneConfigService.findAll();
    }

    @GetMapping("/test")
    public String test() {
        log.info("Test");
        sceneConfigWebSocket.sendInfo("1", "te");
        return messageSource.getMessage("te", null, LocaleContextHolder.getLocale());
    }
}
