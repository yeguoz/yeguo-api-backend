package com.yeguo.yeguoapi.service;

import com.yeguo.yeguoapi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.yeguo.yeguoapi.model.dto.interfaceInfo.InterfaceInfoRegisterRequest;
import com.yeguo.yeguoapi.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.yeguo.yeguoapi.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yeguo.yeguoapi.model.vo.InterfaceInfoVO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;

/**
* @author yeguo
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2024-05-08 18:58:36
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

  Long interfaceInfoRegister(InterfaceInfoRegisterRequest interfaceInfoRegisterRequest, HttpServletRequest req);

  int rmByid(Long id);

  int upById(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,HttpServletRequest req);

  ArrayList<InterfaceInfoVO> selectAll();

  ArrayList<InterfaceInfoVO> dynamicQuery(InterfaceInfoQueryRequest interfaceInfoQueryRequest);
}
