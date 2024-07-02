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

    /**
     * toListBOResult
     *
     * @param modelResult
     * @param tClass
     * @return
     */
    public static <T extends ModelBO<M, T>, M extends BaseModel> ListObjServiceResult<T> toListBOResult(
            ListObjServiceResult<M> modelResult, Class<T> tClass) {
        modelResult = modelResult.asResult();
        ListObjResult<M> modelData = modelResult.getData();
        ListObjResult<T> boSource = null;
        if (modelData != null) {
            List<T> bos = new ArrayList<>();
            for (M obj : modelData.getList()) {
                T t = ClassUtils.createInstance(tClass).fromModel(obj);
                bos.add(t);
            }
            boSource = new ListObjResult<>(bos, modelData.getTotal());
        }
        return new ListObjServiceResult<>(boSource, modelResult.getServiceCode(), modelResult.getDetail());
    }

    /**
     * toBOResult
     *
     * @param modelResult
     * @param tClass
     * @return
     */
    public static <T extends ModelBO<M, T>, M extends BaseModel> ObjServiceResult<T> toBOResult(
            ObjServiceResult<M> modelResult, Class<T> tClass) {
        modelResult = modelResult.asResult();
        M modelData = modelResult.getData();
        ObjResult<T> boSource = null;
        if (modelData != null) {
            T bo = ClassUtils.createInstance(tClass).fromModel(modelData);
            boSource = new ObjResult<>(bo);
        }
        return new ObjServiceResult<>(boSource, modelResult.getServiceCode(), modelResult.getDetail());
    }

    /**
     * exportListExcel
     *
     * @param modelResult
     * @param os
     * @param textResourceClass
     */
    public static <M extends BaseModel, T extends ModelBO<M, T>> void exportListExcel(
            ListObjServiceResult<M> modelResult, OutputStream os, Class<T> textResourceClass) {
        ListObjResult<M> modelData = modelResult.asResult().getData();
        List<M> objs;
        if (modelData == null || modelData.getList() == null) {
            objs = new ArrayList<>();
        } else {
            objs = modelData.getList();
        }
        List<T> exportData = new ArrayList<>();
        for (M obj : objs) {
            T t = ClassUtils.createInstance(textResourceClass).fromModel(obj);
            exportData.add(t);
        }
        if (exportData.isEmpty()) {
            ExcelCommon.writeHeaderOnly(textResourceClass, os);
        } else {
            ExcelCommon.write(exportData, os);
        }
    }

    /**
     * importListExcel
     *
     * @param is
     * @param textResourceClass
     * @return
     */
    public static <M extends BaseModel, T extends ModelBO<M, T>> List<M> importListExcel(InputStream is,
                                                                                         Class<T> textResourceClass) {
        List<T> bos = ExcelCommon.read(is, textResourceClass);
        List<M> list = new ArrayList<>();
        for (T bo : bos) {
            M model = bo.toModel();
            list.add(model);
        }
        return list;
    }

}
