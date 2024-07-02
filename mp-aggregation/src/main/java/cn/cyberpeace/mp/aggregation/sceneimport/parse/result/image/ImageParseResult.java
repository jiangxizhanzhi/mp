package cn.cyberpeace.mp.aggregation.sceneimport.parse.result.image;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @ClassName ImageParseResult
 * @Description
 * @Author maojun
 * @Date 2021/11/2
 * @Version 1.0.0
 */
@Setter
@Getter
public class ImageParseResult {
    private Map<String, ImageInfoParseResult> servers;
}
