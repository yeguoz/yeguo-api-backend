package icu.yeguo.yeguoapi.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.common.Result;
import icu.yeguo.yeguoapi.common.ResultUtils;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.interfaceInfo.*;
import icu.yeguo.yeguoapi.model.vo.InterfaceInfoVO;
import icu.yeguo.yeguoapi.service.InterfaceInfoService;
import icu.yeguo.yeguoapisdk.client.YGAPIClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static icu.yeguo.yeguoapi.utils.IsAdminUtil.isAdmin;

@Slf4j
@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {

    @Autowired
    InterfaceInfoService interfaceInfoServiceImpl;

    @Autowired
    YGAPIClient ygapiClient;

    /*
     *  注册
     * */
    @PostMapping("register")
    public Result<Long> interfaceInfoRegister(@RequestBody InterfaceInfoRegisterRequest interfaceInfoRegisterRequest, HttpServletRequest req) {
        if (interfaceInfoRegisterRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = interfaceInfoServiceImpl.interfaceInfoRegister(interfaceInfoRegisterRequest, req);
        return ResultUtils.success(id);
    }

    /*
     *  删除
     * */
    @DeleteMapping("{id}")
    public Result<Integer> removeById(@PathVariable Long id, HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // 删除成功返回值为 1
        int result = interfaceInfoServiceImpl.rmByid(id);
        return ResultUtils.success(result);
    }

    /*
     *  修改
     * */
    @PutMapping("/update")
    public Result<Integer> updateById(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                      HttpServletRequest req) {
        if (!isAdmin(req)) {
            throw new BusinessException(ResponseCode.NO_AUTH_ERROR, "普通用户,无权限执行此操作");
        }
        // 更新成功返回值为 1
        int result = interfaceInfoServiceImpl.upById(interfaceInfoUpdateRequest, req);
        return ResultUtils.success(result);
    }

    /*
     *  查询
     * */
    @GetMapping("dynamicQuery")
    public Result<ArrayList<InterfaceInfoVO>> dynamicQuery(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        ArrayList<InterfaceInfoVO> interfaceInfoVOList;

        if (BeanUtil.isEmpty(interfaceInfoQueryRequest)) {
            interfaceInfoVOList = interfaceInfoServiceImpl.selectAll();
        } else {
            interfaceInfoVOList = interfaceInfoServiceImpl.dynamicQuery(interfaceInfoQueryRequest);
        }
        return ResultUtils.success(interfaceInfoVOList);
    }

    @PostMapping("onlineInvoking")
    public Result<String> onlineInvoking(@RequestBody InvokingRequest invokingRequest) {
        // todo
        // 校验 ak和sk
        System.out.println("request:"+invokingRequest);
        System.out.println("irp:"+ Arrays.toString(invokingRequest.getIrp()));
        InvokingRequestParams[] irp = invokingRequest.getIrp();
        String result = null;
        // todo
        /*
        * [{"id":1717209446502,"index":0,"name":"dsfdsf","value":"dsfsdf"},
        * {"id":1717209452591,"index":1,"name":"dsfsdfsdf","value":"fsdfsdf"}]
        * 遍历数据，把name和value，加入paramMap中，设置其他请求头等
        * */
//        {[{},{}],method,url}
        // GET 请求
        if ("GET".equals(invokingRequest.getMethod())) {
            //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
            HashMap<String, Object> paramMap = new HashMap<>();
            for (InvokingRequestParams item : irp) {
                paramMap.put(item.getName(), item.getValue());
            }
            result = HttpUtil.get(invokingRequest.getUrl(), paramMap);
//            JSONObject jsonObject = JSONUtil.parseObj(result);
//            if ((int)jsonObject.get("code")==400) {
//                throw new BusinessException(ResponseCode.PARAMS_ERROR,(String)jsonObject.get("msg"));
//            }
//            if ((int)jsonObject.get("code")==500) {
//                throw new BusinessException(ResponseCode.SYSTEM_ERROR,(String)jsonObject.get("msg"));
//            }
            System.out.println(result);
        }
        // POST 请求
        if ("POST".equals(invokingRequest.getMethod())) {
            HashMap<String, Object> paramMap = new HashMap<>();
            for (InvokingRequestParams item : irp) {
                paramMap.put(item.getName(), item.getValue());

            }
            result= HttpUtil.post(invokingRequest.getUrl(), paramMap);
        }

        return ResultUtils.success(result);
    }

}
