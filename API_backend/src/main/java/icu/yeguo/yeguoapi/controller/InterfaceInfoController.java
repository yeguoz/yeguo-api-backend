package icu.yeguo.yeguoapi.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.common.Result;
import icu.yeguo.yeguoapi.common.ResultUtils;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.interfaceInfo.*;
import icu.yeguo.yeguoapi.model.vo.InterfaceInfoVO;
import icu.yeguo.yeguoapi.service.InterfaceInfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static icu.yeguo.apicommon.constant.common.*;
import static icu.yeguo.yeguoapi.utils.IsAdminUtil.isAdmin;

@Slf4j
@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {

    @Autowired
    private InterfaceInfoService interfaceInfoServiceImpl;

    /*
     *  注册
     * */
    @PostMapping("/register")
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
    @DeleteMapping("/{id}")
    public Result<Integer> removeById(@PathVariable("id") Long id, HttpServletRequest req) {
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
    @GetMapping("/dynamicQuery")
    public Result<ArrayList<InterfaceInfoVO>> dynamicQuery(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        ArrayList<InterfaceInfoVO> interfaceInfoVOList;

        if (BeanUtil.isEmpty(interfaceInfoQueryRequest)) {
            interfaceInfoVOList = interfaceInfoServiceImpl.selectAll();
        } else {
            interfaceInfoVOList = interfaceInfoServiceImpl.dynamicQuery(interfaceInfoQueryRequest);
        }
        return ResultUtils.success(interfaceInfoVOList);
    }


    @PostMapping("/onlineInvoking")
    public Result<String> onlineInvoking(@RequestPart("invokingRequest") InvokingRequest invokingRequest,
                                         @RequestPart(value = "file", required = false) MultipartFile file,
                                         HttpServletRequest request) throws IOException {
        /*
         * InvokingRequest: {irp:[{},{}],method:"string",url:"string"}
         * */
        String signature = request.getHeader(X_SIGNATURE);
        String accessKey = request.getHeader(X_ACCESS_KEY);
        String timestamp = request.getHeader(X_TIMESTAMP);
        String nonce = request.getHeader(X_NONCE);

        log.info("invokingRequest:{}", invokingRequest);
        log.info("irp:{}", Arrays.toString(invokingRequest.getIrp()));
        log.info("accessKey:{}", accessKey);
        log.info("timestamp:{}", timestamp);
        log.info("nonce:{}", nonce);
        log.info("signature:{}", signature);

        InvokingRequestParams[] irp = invokingRequest.getIrp();
        String result = null;
        HashMap<String, Object> paramMap = new HashMap<>();

        // GET 请求
        if (GET.equals(invokingRequest.getMethod())) {
            // 请求中携带的请求参数,网关处验证
            for (InvokingRequestParams item : irp) {
                paramMap.put(item.getName(), item.getValue());
            }
            result = HttpRequest.get(invokingRequest.getUrl())
                    .header(X_ONLINE_INVOKING,GET)
                    .header(X_ACCESS_KEY,accessKey)
                    .header(X_TIMESTAMP,timestamp)
                    .header(X_NONCE,nonce)
                    .header(X_SIGNATURE,signature)
                    .form(paramMap)
                    .timeout(6 * 60 * 60 * 1000)//超时，毫秒
                    .execute()
                    .body();
        } else if (POST.equals(invokingRequest.getMethod())) {
            for (InvokingRequestParams item : irp) {
                paramMap.put(item.getName(), item.getValue());
            }
            // 上传文件请求
            if (file != null) {
                File tempFile = File.createTempFile("upload", file.getOriginalFilename());
                file.transferTo(tempFile);
                paramMap.put(FILE, tempFile);
                result = HttpRequest.post(invokingRequest.getUrl())
                        .header("Content-Type", "multipart/form-data")
                        .header(X_ONLINE_INVOKING,POST)
                        .header(X_ACCESS_KEY,accessKey)
                        .header(X_TIMESTAMP,timestamp)
                        .header(X_NONCE,nonce)
                        .header(X_SIGNATURE,signature)
                        .form(paramMap)
                        .timeout(6 * 60 * 60 * 1000)
                        .execute()
                        .body();
            }else { // 其他POST请求
                result = HttpRequest.post(invokingRequest.getUrl())
                        .header(X_ONLINE_INVOKING,POST)
                        .header(X_ACCESS_KEY,accessKey)
                        .header(X_TIMESTAMP,timestamp)
                        .header(X_NONCE,nonce)
                        .header(X_SIGNATURE,signature)
                        .form(paramMap)
                        .timeout(6 * 60 * 60 * 1000)
                        .execute()
                        .body();
            }
        }
        return ResultUtils.success(result);
    }
}
