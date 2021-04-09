package cn.cyberpeace.mp.scene.controller;

import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.websocket.SceneConfigWebSocket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SceneConfigController.class)
@AutoConfigureRestDocs
public class SceneConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SceneConfigService sceneConfigService;

    @MockBean
    private SceneConfigWebSocket sceneConfigWebSocket;

    @Test
    public void test() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("测试")))
                .andDo(document("home"));
    }
}
