package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Setter
@Getter
public class ImageParseResult {
    private Map<String, ImageInfoParseResult> servers;
}
