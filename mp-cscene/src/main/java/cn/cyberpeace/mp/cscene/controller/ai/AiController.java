package cn.cyberpeace.mp.cscene.controller.ai;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.core.BcSceneConfigHandler;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Api(value = "ai", tags = "CSCENE-ai")
@Slf4j
@RestController
@RequestMapping("/unauth/ai")
@Data
@AuthTarget({TargetEnum.NO_AUTH})
public class AiController {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Resource
    private CsceneService csceneService;

//    @CrossOrigin(origins = "*")
//    @ApiOperation("ai chat")
//    @GetMapping(value = "/chat/completions", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter statDeviceReferCscenes(@RequestBody(required = false) CompletionBO completionBO) {
//        String clientId = completionBO == null ? TextUtils.uuid() : completionBO.getConversationId();
//        SseEmitter emitter = SseEmitterHandler.build(clientId);
//        String message = MessageBuilder.node();
//        if (StringUtils.isNotEmpty(message)) {
//            new Thread(() -> SseEmitterHandler.send(emitter, message)).start();
//        }
//        return emitter;
//    }

    @CrossOrigin(origins = "*")
    @ApiOperation("ai chat")
    @GetMapping(value = "/chat/data")
    public ServiceResult<List<Message>> statDeviceReferCscenes2(@RequestParam("cscene_id") String csceneId, String type, String message) {
        if ("add_node".equals(type)) {
            // 添加节点 返回固定的结果
            return new ServiceResult<>(MessageBuilder.node()).asResult();
        } else if ("add_topo".equals(type)) {
            // 加拓扑 先把固定的拓扑保存成一个场景，得到单独的id，这样返回的结果每次都会有不同，看起来真实一点
            // 查询拓扑 然后保存得到一个id
            var scene = MessageBuilder.getTopo();
            CsceneBO csceneBO = new CsceneBO();
            csceneBO.setSceneConfig(scene);
            BcSceneConfigHandler bcSceneConfigHandler = new BcSceneConfigHandler(csceneBO, new CsceneContextBO());
            bcSceneConfigHandler.save();
            String templateId = bcSceneConfigHandler.getCsceneId();

            // 把拓扑的字符串内容保存到文件
            var configFileUrl = FsCommon.uploadContent(JsonCommon.stringify(scene));
            configFileUrl = FsCommon.genInnerHttpUrl(configFileUrl);

            // 返回结果
            var res = MessageBuilder.topo(csceneId, templateId, configFileUrl);
            return new ServiceResult<>(res).asResult();
        } else if ("update_config".equals(type)) {
            // 从message里面提取要改的内存和磁盘大小?  能不提取就提取，简单做
            String ram = "4";
            String cpu = "4096";
            String disk = "80";

            // 根据聊天框里的message内容，筛选需要修改的节点，只支持linux或者windows类型节点的修改
            List<SceneConfigElementBO> elements = new ArrayList<>();
            var sceneId = csceneService.get(csceneId).asResult().getData().getSceneConfig();
            SceneConfigHandler sceneHandler = new SceneConfigHandler(sceneId, null, null);
            boolean toUpdateLinux = message.toLowerCase().contains("linux");
            boolean toUpdateWindows = message.toLowerCase().contains("win");
            CloudSceneConfigPluginHandler pluginHandler = sceneHandler.getPluginHandler("cpcloud");
            for (SceneConfigElementBO element : sceneHandler.getElementMap().values()) {
                var cloudElement = pluginHandler.getElement(element.getId());
                if (cloudElement.getVirtualType() != BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE && cloudElement.getVirtualType() != BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                    continue;
                }
                if (toUpdateLinux && SystemTypeCommandUtils.isLinux(element.getSystemType())) {
                    // 改linux的内存和磁盘大小
                    elements.add(element);
                } else if (toUpdateWindows && SystemTypeCommandUtils.isWindows(element.getSystemType())) {
                    elements.add(element);
                }
            }

            // 获取返回内容的字符串
            var config = MessageBuilder.config();
            // 列表的最后一个，是每个节点描述的模板
            var templateMessage = config.remove(config.size() - 1);
            var templateContent = templateMessage.getContent();
            var buttonTxt = templateMessage.getButton();
            // 根据描述的模板和筛选出的节点，设定每个节点的描述内容
            if (elements.size() > 0) {
                for (SceneConfigElementBO element : elements) {
                    var m = new Message();
                    String originCpu = element.getCpu() == null || element.getCpu() == 0 ? "1" : String.valueOf(element.getCpu());
                    String originRam = element.getRam() == null || element.getRam() == 0 ? "1" : String.valueOf(element.getRam());
                    String originDisk = element.getDisk() == null || element.getDisk() == 0 ? "1" : String.valueOf(element.getDisk());

                    String content = templateContent;
                    content = StringUtils.replace(content, "{name}", element.getName());
                    content = StringUtils.replace(content, "{origin_cpu}", originCpu);
                    content = StringUtils.replace(content, "{origin_ram}", originRam);
                    content = StringUtils.replace(content, "{origin_disk}", originDisk);
                    content = StringUtils.replace(content, "{cpu}", cpu);
                    content = StringUtils.replace(content, "{ram}", ram);
                    content = StringUtils.replace(content, "{disk}", disk);
                    m.setContent(content);
                    m.setType("update_config");
                    m.setCpu(Integer.parseInt(cpu));
                    m.setRam(Integer.parseInt(ram));
                    m.setDisk(Integer.parseInt(disk));
                    m.setNodeId(element.getId());
                    m.setButton(buttonTxt);
                    config.add(m);
                }
            }
            // 返回结果
            return new ServiceResult<>(config).asResult();
        } else {
            return new ServiceResult<List<Message>>().asResult();
        }
    }

    @Getter
    @Setter
    public static class Message {
        @ApiModelProperty(value = "消息内容")
        private String content;
        @ApiModelProperty(value = "按钮显示文字")
        private String button;
        @ApiModelProperty(value = "消息类型: add_node,加节点 ; add_topo，加拓扑; update_config，更改配置")
        private String type;
        @ApiModelProperty(value = "点击按钮使用的url")
        private String url;
        @ApiModelProperty(value = "更改配置时，指定的节点id")
        private String nodeId;
        @ApiModelProperty(value = "更改配置时，指定的cpu")
        private int cpu;
        @ApiModelProperty(value = "更改配置时，指定的磁盘")
        private int disk;
        @ApiModelProperty(value = "更改配置时，指定的内存")
        private int ram;
    }

    @Getter
    @Setter
    public static class CompletionBO {
        private String conversationId;
        private String agent;
        private String token;
        private Boolean stream;
        private List<CompletionMessageBO> messages;
    }

    @Getter
    @Setter
    public static class CompletionMessageBO {
        private String user;
        private String content;
    }

}
