package icu.yeguo.apicommon.service;

import icu.yeguo.apicommon.model.entity.User;

public interface CommonService {
    String sayHello(String name);
    User getUser(String accessKey);
    String generateSignature(String message,User user);
    Long invokingCount(long interfaceInfoId);
    Long getInterfaceInfoId(String url);
    User deductGoldCoin(Long interfaceInfoId,User user);
    Long returnGoldCoins(Long interfaceInfoId, User user);
}
