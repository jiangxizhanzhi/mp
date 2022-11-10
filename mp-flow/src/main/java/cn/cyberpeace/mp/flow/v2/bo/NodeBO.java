package cn.cyberpeace.mp.flow.v2.bo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class NodeBO {
    private String name;

    private String resourceId;

    public static Map<String, String> VALUE = new HashMap<>() {{
        this.put("9345f211-264b-4fa2-84ea-97338b430454", "target_1");
        this.put("97e0fc80-5fb1-4f1a-bcca-64b0ecec43ee", "target_2");
        this.put("28de2fde-ce4d-479b-a176-407437637dd1", "target_3");
        this.put("4f792ffe-41b8-4320-b58f-955ae0d647ba", "target_4");
        this.put("0bbc1b24-5143-487a-93e8-64afac7de039","target_ubuntu");
    }};

}