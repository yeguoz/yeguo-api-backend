package icu.yeguo.yeguoapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.yeguo.yeguoapi.common.ResponseCode;
import icu.yeguo.yeguoapi.exception.BusinessException;
import icu.yeguo.yeguoapi.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import icu.yeguo.yeguoapi.model.dto.interfaceInfo.InterfaceInfoRegisterRequest;
import icu.yeguo.yeguoapi.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import icu.yeguo.yeguoapi.model.entity.InterfaceInfo;
import icu.yeguo.yeguoapi.model.vo.InterfaceInfoVO;
import icu.yeguo.yeguoapi.model.vo.UserVO;
import icu.yeguo.yeguoapi.service.InterfaceInfoService;
import icu.yeguo.yeguoapi.mapper.InterfaceInfoMapper;
import icu.yeguo.yeguoapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author yeguo
 * @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
 * @createDate 2024-05-08 18:58:36
 */

@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {

    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;
    @Autowired
    private UserService userServiceImpl;

    /**
     * 接口注册
     * @params  interfaceInfoRegisterRequest
     * @params req
     * @return Long
     * @author yeguo
     */
    @Override
    public Long interfaceInfoRegister(InterfaceInfoRegisterRequest interfaceInfoRegisterRequest, HttpServletRequest req) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        UserVO currentUser = userServiceImpl.getCurrentUser(req);
        interfaceInfo.setUserId(currentUser.getId());

        interfaceInfo.setName(interfaceInfoRegisterRequest.getName());
        interfaceInfo.setDescription(interfaceInfoRegisterRequest.getDescription());
        interfaceInfo.setMethod(interfaceInfoRegisterRequest.getMethod());
        interfaceInfo.setUrl(interfaceInfoRegisterRequest.getUrl());
        interfaceInfo.setRequestParams(interfaceInfoRegisterRequest.getRequestParams());
        interfaceInfo.setRequestHeader(interfaceInfoRegisterRequest.getRequestHeader());
        interfaceInfo.setResponseHeader(interfaceInfoRegisterRequest.getRequestHeader());
        interfaceInfo.setResponseFormat(interfaceInfoRegisterRequest.getResponseFormat());
        interfaceInfo.setRequestExample(interfaceInfoRegisterRequest.getRequestExample());
        interfaceInfo.setInterfaceStatus(interfaceInfoRegisterRequest.getInterfaceStatus());
        interfaceInfo.setInvokingCount(interfaceInfoRegisterRequest.getInvokingCount());
        interfaceInfo.setAvatarUrl(interfaceInfoRegisterRequest.getAvatarUrl());
        interfaceInfo.setRequiredGoldCoins(interfaceInfoRegisterRequest.getRequiredGoldCoins());

        int result;
        try {
            result = interfaceInfoMapper.insert(interfaceInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (result < 0)
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "添加失败，请检查代码");

        return interfaceInfo.getId();
    }

    /**
     * @param id
     * @return int
     * @author yeguo
     */
    @Override
    public int rmByid(Long id) {
        int result;
        try {
            result = interfaceInfoMapper.deleteById(id);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (result < 1)
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "删除失败,该用户不存在或其他问题");
        return result;
    }

    /**
     * 根据id更新
     * @param interfaceInfoUpdateRequest
     * @param req
     * @return int
     * @author yeguo
     */
    @Override
    public int upById(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest req) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        Long currentUserId = userServiceImpl.getCurrentUser(req).getId();

        interfaceInfo.setId(interfaceInfoUpdateRequest.getId());
        interfaceInfo.setUserId(currentUserId);
        interfaceInfo.setName(interfaceInfoUpdateRequest.getName());
        interfaceInfo.setDescription(interfaceInfoUpdateRequest.getDescription());
        interfaceInfo.setMethod(interfaceInfoUpdateRequest.getMethod());
        interfaceInfo.setUrl(interfaceInfoUpdateRequest.getUrl());
        interfaceInfo.setRequestParams(interfaceInfoUpdateRequest.getRequestParams());
        interfaceInfo.setRequestHeader(interfaceInfoUpdateRequest.getRequestHeader());
        interfaceInfo.setResponseHeader(interfaceInfoUpdateRequest.getResponseHeader());
        interfaceInfo.setResponseFormat(interfaceInfoUpdateRequest.getResponseFormat());
        interfaceInfo.setRequestExample(interfaceInfoUpdateRequest.getRequestExample());
        interfaceInfo.setInterfaceStatus(interfaceInfoUpdateRequest.getInterfaceStatus());
        interfaceInfo.setInvokingCount(interfaceInfoUpdateRequest.getInvokingCount());
        interfaceInfo.setAvatarUrl(interfaceInfoUpdateRequest.getAvatarUrl());
        interfaceInfo.setRequiredGoldCoins(interfaceInfoUpdateRequest.getRequiredGoldCoins());

        int result;
        try {
            result = interfaceInfoMapper.updateById(interfaceInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (result < 1)
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "更新失败,该用户不存在或其他问题");
        return result;
    }

    /**
     * 查询所有接口
     * @return ArrayList<InterfaceInfoVO>
     * @author yeguo
     */
    @Override
    public ArrayList<InterfaceInfoVO> selectAll() {
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 查询所有接口
        ArrayList<InterfaceInfo> interfaceInfoList;
        try {
            interfaceInfoList = (ArrayList<InterfaceInfo>) interfaceInfoMapper.selectList(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (interfaceInfoList == null)
            throw new BusinessException(ResponseCode.NOT_FOUND_ERROR, "查询为空,不存在数据或其他问题");

        // 对每个接口脱敏 返回安全用户信息
        ArrayList<InterfaceInfoVO> result = new ArrayList<>();
        for (InterfaceInfo interfaceInfo : interfaceInfoList) {
            InterfaceInfoVO interfaceInfoVO = getInterfaceInfoVO(interfaceInfo);
            result.add(interfaceInfoVO);
        }
        return result;

    }

    /**
     * 根据请求参数动态查询
     * @params  interfaceInfoQueryRequest
     * @return ArrayList<InterfaceInfoVO>
     * @author yeguo
     */
    @Override
    public ArrayList<InterfaceInfoVO> dynamicQuery(InterfaceInfoQueryRequest interfaceInfoQueryRequest) {
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(interfaceInfoQueryRequest.getId() != null, InterfaceInfo::getId, interfaceInfoQueryRequest.getId())
                .eq(interfaceInfoQueryRequest.getUserId() != null, InterfaceInfo::getUserId, interfaceInfoQueryRequest.getUserId())
                .eq(interfaceInfoQueryRequest.getName() != null, InterfaceInfo::getName, interfaceInfoQueryRequest.getName())
                .eq(interfaceInfoQueryRequest.getDescription() != null, InterfaceInfo::getDescription, interfaceInfoQueryRequest.getDescription())
                .eq(interfaceInfoQueryRequest.getMethod() != null, InterfaceInfo::getMethod, interfaceInfoQueryRequest.getMethod())
                .eq(interfaceInfoQueryRequest.getUrl() != null, InterfaceInfo::getUrl, interfaceInfoQueryRequest.getUrl())
                .eq(interfaceInfoQueryRequest.getResponseFormat() != null, InterfaceInfo::getResponseFormat, interfaceInfoQueryRequest.getResponseFormat())
                .eq(interfaceInfoQueryRequest.getInvokingCount() != null, InterfaceInfo::getInvokingCount, interfaceInfoQueryRequest.getInvokingCount())
                .eq(interfaceInfoQueryRequest.getRequiredGoldCoins() != null, InterfaceInfo::getRequiredGoldCoins, interfaceInfoQueryRequest.getRequiredGoldCoins());
        ArrayList<InterfaceInfo> interfaceInfoList;
        ArrayList<InterfaceInfoVO> result = new ArrayList<>();
        // 防止出现操作数据库错误
        try {
            interfaceInfoList = (ArrayList<InterfaceInfo>) interfaceInfoMapper.selectList(lambdaQueryWrapper);
            for (InterfaceInfo interfaceInfo : interfaceInfoList) {
                InterfaceInfoVO interfaceInfoVO = getInterfaceInfoVO(interfaceInfo);
                result.add(interfaceInfoVO);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public InterfaceInfoVO getInterfaceInfoVO(InterfaceInfo interfaceInfo) {
        InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
        interfaceInfoVO.setId(interfaceInfo.getId());
        interfaceInfoVO.setUserId(interfaceInfo.getUserId());
        interfaceInfoVO.setName(interfaceInfo.getName());
        interfaceInfoVO.setDescription(interfaceInfo.getDescription());
        interfaceInfoVO.setMethod(interfaceInfo.getMethod());
        interfaceInfoVO.setUrl(interfaceInfo.getUrl());
        interfaceInfoVO.setRequestParams(interfaceInfo.getRequestParams());
        interfaceInfoVO.setResponseParams(interfaceInfo.getResponseParams());
        interfaceInfoVO.setResponseFormat(interfaceInfo.getResponseFormat());
        interfaceInfoVO.setRequestExample(interfaceInfo.getRequestExample());
        interfaceInfoVO.setResponseExample(interfaceInfo.getResponseExample());
        interfaceInfoVO.setInterfaceStatus(interfaceInfo.getInterfaceStatus());
        interfaceInfoVO.setInvokingCount(interfaceInfo.getInvokingCount());
        interfaceInfoVO.setAvatarUrl(interfaceInfo.getAvatarUrl());
        interfaceInfoVO.setRequiredGoldCoins(interfaceInfo.getRequiredGoldCoins());
        interfaceInfoVO.setRequestHeader(interfaceInfo.getRequestHeader());
        interfaceInfoVO.setResponseHeader(interfaceInfo.getResponseHeader());
        interfaceInfoVO.setCreateTime(interfaceInfo.getCreateTime());
        return interfaceInfoVO;
    }

}




