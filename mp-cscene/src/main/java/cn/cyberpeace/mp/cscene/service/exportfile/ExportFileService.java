package cn.cyberpeace.mp.cscene.service.exportfile;

import cn.cyberpeace.mp.base.service.param.QueryParams;

import javax.servlet.http.HttpServletResponse;

public interface ExportFileService {

    public <D, R extends QueryParams> void exportFile(HttpServletResponse httpServletResponse, PageIterator<D, R> pageIterator, String filename);
}
