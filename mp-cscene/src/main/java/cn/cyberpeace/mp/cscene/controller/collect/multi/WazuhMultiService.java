package cn.cyberpeace.mp.cscene.controller.collect.multi;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhParsedResult;
import cn.cyberpeace.mp.cscene.controller.collect.base.WazuhQueryService;
import cn.cyberpeace.mp.cscene.controller.collect.filemodify.WazuhFileModifyDataParser;
import cn.cyberpeace.mp.cscene.controller.collect.filemodify.WazuhFileModifyDataQueryParamsBuilder;
import cn.cyberpeace.mp.cscene.controller.collect.process.WazuhProcessDataParser;
import cn.cyberpeace.mp.cscene.controller.collect.process.WazuhProcessDataQueryParamsBuilder;
import cn.cyberpeace.mp.cscene.controller.collect.user.WazuhSystemUserDataParser;
import cn.cyberpeace.mp.cscene.controller.collect.user.WazuhSystemUserDataQueryParamsBuilder;
import cn.cyberpeace.mp.situationaware.model.FileEventModel;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WazuhMultiService {


}
