package icu.yeguo.yeguoapi.service.impl.provider;


import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.apicommon.service.CommonService;
import icu.yeguo.yeguoapi.mapper.InterfaceInfoMapper;
import icu.yeguo.yeguoapi.mapper.UserMapper;
import icu.yeguo.yeguoapi.model.entity.InterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DubboService
public class CommonServiceImpl implements CommonService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public String sayHello(String name) {
        System.out.println("DubboService_name:" + name);
        return name;
    }

    @Override
    public User getUser(String accessKey) {
        // 查询数据库
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccessKey, accessKey);
        return userMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public String generateSignature(String message, User user) {
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, user.getSecretKey().getBytes());
        return mac.digestHex(message);
    }

    @Transactional
    @Override
    public Long invokingCount(long interfaceInfoId) {
        // 查询当前接口
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceInfoId);
        Long invokingCount = interfaceInfo.getInvokingCount();
        // 更新 ID 为 interfaceInfoId 的接口的调用次数
        InterfaceInfo updateInterfaceInfo = new InterfaceInfo();
        updateInterfaceInfo.setId(interfaceInfoId);
        updateInterfaceInfo.setInvokingCount(++invokingCount);
        int rows;
        InterfaceInfo updatedInterfaceInfo;
        try {
            rows = interfaceInfoMapper.updateById(updateInterfaceInfo);
            if (rows < 0) {
                log.warn("接口调用次数更新失败");
                return -1L;
            }
            log.info("接口调用次数更新成功====" + interfaceInfo.getName());
            updatedInterfaceInfo = interfaceInfoMapper.selectById(interfaceInfoId);
            if (updatedInterfaceInfo == null)
                return -1L;
        } catch (Exception e) {
            log.error("invokingCount error", e);
            throw new RuntimeException(e);
        }
        return updatedInterfaceInfo.getInvokingCount();
    }

    @Override
    public Long getInterfaceInfoId(String url) {
        // 查询数据库中url url是interfaceInfo的接口地址
        LambdaQueryWrapper<InterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(InterfaceInfo::getUrl, url);
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(lambdaQueryWrapper);
        return interfaceInfo != null ? interfaceInfo.getId() : null;
    }

    @Transactional
    @Override
    public User deductGoldCoin(Long interfaceInfoId, User user) {
        try {
            InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceInfoId);
            if (interfaceInfo == null)
                return null;
            // 消耗金币
            Long requiredGoldCoins = interfaceInfo.getRequiredGoldCoins();
            // 设置用户，设置消费后的金币
            if (user.getGoldCoin() < requiredGoldCoins)
                return null;

            user.setGoldCoin(user.getGoldCoin() - requiredGoldCoins);
            // 更新用户信息
            int i = userMapper.updateById(user);
            if (i < 0)
                return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, user.getId()));
    }

    @Transactional
    @Override
    public Long returnGoldCoins(Long interfaceInfoId, User user) {
        User dataUser;
        try {
            // 查询接口 所需金币
            InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceInfoId);
            if (interfaceInfo == null)
                return -1L;
            // 查询用户 为用户加上被扣掉的金币
            dataUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, user.getId()));
            if (dataUser == null)
                return -1L;
            dataUser.setGoldCoin(dataUser.getGoldCoin() + interfaceInfo.getRequiredGoldCoins());
            // 更新用户信息
            int i = userMapper.updateById(dataUser);
            if (i < 0)
                return -1L;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dataUser.getGoldCoin();
    }
}
