package cn.cyberpeace.mp.base.service.common;

import cn.cyberpeace.mp.base.common.textresource.exel.ExcelCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.service.bo.ModelBO;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModelServiceCommon {

    public static <T extends ModelBO<M, T>, M extends BaseModel> ListObjServiceResult<T> toListBOResult(
            ListObjServiceResult<M> modelResult, Class<T> tClass) {
        modelResult = modelResult.asResult();
        var modelData = modelResult.getData();
        ListObjResult<T> boSource = null;
        if (modelData != null) {
            var bos = modelData.getList().stream()
                    .map(obj -> ClassUtils.createInstance(tClass).fromModel(obj))
                    .collect(Collectors.toList());
            boSource = new ListObjResult<>(bos, modelData.getTotal());
        }
        return new ListObjServiceResult<>(boSource, modelResult.getServiceCode(), modelResult.getDetail());
    }

    public static <T extends ModelBO<M, T>, M extends BaseModel> ObjServiceResult<T> toBOResult(
            ObjServiceResult<M> modelResult, Class<T> tClass) {
        modelResult = modelResult.asResult();
        var modelData = modelResult.getData();
        ObjResult<T> boSource = null;
        if (modelData != null) {
            var bo = ClassUtils.createInstance(tClass).fromModel(modelData);
            boSource = new ObjResult<>(bo);
        }
        return new ObjServiceResult<>(boSource, modelResult.getServiceCode(), modelResult.getDetail());
    }

    public static <M extends BaseModel, T extends ModelBO<M, T>> void exportListExcel(
            ListObjServiceResult<M> modelResult, OutputStream os, Class<T> textResourceClass) {
        var modelData = modelResult.asResult().getData();
        List<M> objs;
        if (modelData == null || modelData.getList() == null) {
            objs = new ArrayList<>();
        } else {
            objs = modelData.getList();
        }
        var exportData = objs.stream()
                .map(obj -> ClassUtils.createInstance(textResourceClass).fromModel(obj))
                .collect(Collectors.toList());
        if (exportData.isEmpty()) {
            ExcelCommon.writeHeaderOnly(textResourceClass, os);
        } else {
            ExcelCommon.write(exportData, os);
        }
    }

    public static <M extends BaseModel, T extends ModelBO<M, T>> List<M> importListExcel(InputStream is,
                                                                                         Class<T> textResourceClass) {
        var bos = ExcelCommon.read(is, textResourceClass);
        return bos.stream().map(bo -> bo.toModel()).collect(Collectors.toList());
    }

}
