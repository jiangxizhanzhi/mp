package cn.cyberpeace.mp.sceneplugingns3.plugin.gns3api.param;

import cn.cyberpeace.mp.sceneplugingns3.util.gns3http.Gns3HttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class LinkNodeParams implements Gns3HttpData {

    private List<LinkNode> nodes;

    @Getter
    @Setter
    public static class LinkNode {
        private String nodeId;
        private Integer portNumber = 0;
        private Integer adapterNumber = 0;
        private Label label = new Label();
    }

    @Getter
    @Setter
    private static class Label {
        private String style = "font-family: TypeWriter;font-size: 10.0;font-weight: bold;fill: #000000;fill-opacity: 1.0;";
        private String text = "a0";
    }
}
