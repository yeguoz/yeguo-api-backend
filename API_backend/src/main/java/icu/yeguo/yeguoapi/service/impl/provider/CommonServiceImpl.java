package icu.yeguo.yeguoapi.service.impl.provider;


import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import icu.yeguo.yeguoapi.constant.SecretConstant;
import icu.yeguo.apicommon.model.entity.User;
import icu.yeguo.apicommon.service.CommonService;
import icu.yeguo.yeguoapi.mapper.UserMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class CommonServiceImpl implements CommonService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String sayHello(String name) {
        System.out.println("DubboService_name:"+name);
        return name;
    }

    @Override
    public User getUser(String accessKey) {
        // 查询数据库
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccessKey, accessKey);
        User user;
        try {
            user = userMapper.selectOne(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public String generateSignature(String message) {
        // 此处密钥如果有非ASCII字符
        byte[] key = SecretConstant.SIGNATURE_KEY.getBytes();
        HMac mac = new HMac(HmacAlgorithm.HmacMD5, key);
        // 生成签名
        return mac.digestHex(message);
    }


}
