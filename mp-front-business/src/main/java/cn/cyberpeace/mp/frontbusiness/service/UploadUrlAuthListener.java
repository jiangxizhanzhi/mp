package cn.cyberpeace.mp.frontbusiness.service;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.frontbusiness.dto.ImportUrlAuth;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class UploadUrlAuthListener extends AnalysisEventListener<ImportUrlAuth> {

    
    private static final int BATCH_COUNT = 10000;

    
    private static final int MAX_COUNT = 10000;

    List<ImportUrlAuth> list = new ArrayList<>();

    private UrlACService urlACService;

    public UploadUrlAuthListener(UrlACService urlACService) {
        this.urlACService = urlACService;
    }

    
    @Override
    public void invoke(ImportUrlAuth data, AnalysisContext context) {
        list.add(data);

        if (list.size() > MAX_COUNT) {
            throw new MpRuntimeException(AuthErrorCode.EXCEL_LIST_SIZE_OVER.format("100"));
        }
    }

    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        
        saveData();
        log.info("所有数据解析完成！");
    }

    
    private void saveData() {
        
        
        
        
        
        
        
        urlACService.importByExcel(list);


        log.info("save success！");
    }



}
