package cn.cyberpeace.mp.front.admin.controller;

import cn.cyberpeace.mp.front.admin.service.sys.activmenu.ActivMenuService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/16 19:02
 */
@Api(value = "ADMIN-TEST", tags = "ADMIN-TEST")
@Slf4j
@RestController
@RequestMapping("/admin/test")
public class AdminTestController {

    @Autowired
    private ActivMenuService activMenuService;


    @PostMapping("/activ/menu")
    public void activMenu(MultipartFile file) {
        activMenuService.process(file);
    }

}
