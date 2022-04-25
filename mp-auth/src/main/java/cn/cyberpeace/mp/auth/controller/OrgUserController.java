package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.BindOrgUsersRequest;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "组织-用户管理接口", tags = "AUTH-组织-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_users")
public class OrgUserController extends BaseModelController<OrgUserModel, QueryParams, OrgUserService> {

    @Autowired
    public OrgUserController(OrgUserService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "分页查询组织下所有用户信息（默认查询组织下所有层级子组织的用户，" +
            "可以通过设置showAllUnderLevel=false来关闭默认，只显示当前组织绑定的用户），附带组织，角色信息", notes = "")
    @GetMapping("/page/byorg")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ListObjServiceResult<Object> getPageByOrg(OrgUserPageRequest req) {
        req.ensure();
        PageInfo pageInfo = getModelService().pageUsersByOrg(req);

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @ApiOperation(value = "查询组织下所有用户信息", notes = "")
    @GetMapping("/list/byorg")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getByOrg(@RequestParam String orgId) {
        List<UserModel> users = getModelService().listUsersByOrgId(orgId);
        return ServiceResult.builder().source(users).build().asResult();
    }


    @ApiOperation(value = "根据userId查询所在组织信息（按照层级排列）", notes = "")
    @GetMapping("/hierarchical/byuser")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult listHierarchicalOrgs(@RequestParam String userId) {
        List<OrgDto> orgDtos = getModelService().listHierarchicalOrgsByUserId(userId);
        return ServiceResult.builder().source(orgDtos).build().asResult();
    }



    @ApiOperation(value = "增量绑定组织-用户关系！", notes = "")
    @PostMapping("/bind")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult bindOrgUsersAdditional(@RequestBody @Validated BindOrgUsersRequest bindOrgUsersReq) {
        boolean success = getModelService().additionalBindOrgUsers(bindOrgUsersReq.getOrgId(), bindOrgUsersReq.getUserIds());
        return ResultUtils.commonResult(success, null);
    }



    @ApiOperation(value = "根据userId查询所有所在组织的路径 如： 赛宁信息/产品研发中心/产品部,赛宁信息/产品研发中心/研发部", notes = "")
    @GetMapping("/allorgpath/byuser")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getAllOrgPath(@RequestParam String userId) {
        String allOrgPath = getModelService().getAllOrgPathByUserId(userId);
        return ServiceResult.builder().source(allOrgPath).build().asResult();
    }







    /**
     * 通用查询方法
     * @param queryParams 查询参数
     * @return 列表查询结果
     */
    @Override
    @ApiOperation("获取分页数据")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ListObjServiceResult<OrgUserModel> list(QueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().list(queryParams).asResult();
    }

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<OrgUserModel> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        log.info("id:{}, fields:{}", id, fields);
        return getModelService().get(id, fields).asResult();
    }

    /**
     * 通用创建
     * @param obj 数据对象
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("创建数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<OrgUserModel> create(@RequestBody OrgUserModel obj, @RequestParam(required = false) String[] fields) {
        return getModelService().create(obj, fields).asResult();
    }

    /**
     * 通用更新
     * @param id 数据id
     * @param obj 数据对象
     * @param fields 返回数据字段，只涉及更新字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("更新指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<OrgUserModel> update(@PathVariable String id, @RequestBody OrgUserModel obj,
                                                 @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return getModelService().update(obj, fields).asResult();
    }

    /**
     * 通用删除
     * @param id 数据id
     * @return 是否删除成功
     */
    @Override
    @ApiOperation("删除指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        log.info("id:{}", id);
        return getModelService().delete(id).asResult();
    }

    /**
     * 通用批量创建
     * @param objs 创建对象集
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量创建数据")
    @PostMapping("/bulk_create")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkCreate(@RequestBody List<OrgUserModel> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

    /**
     * 通用批量更新
     * @param objs 更新对象集，需要更新的字段必须一致
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量更新数据")
    @PatchMapping("/bulk_update")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<OrgUserModel> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

    /**
     * 通用批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量删除数据")
    @DeleteMapping("/bulk_delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }



}
