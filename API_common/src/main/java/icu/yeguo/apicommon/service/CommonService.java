package icu.yeguo.apicommon.service;

import icu.yeguo.apicommon.model.entity.User;

public interface CommonService {
    String sayHello(String name);
    User getUser(String accessKey);
    String generateSignature(String message);
    void invokingCount(long interfaceInfoId);
}
