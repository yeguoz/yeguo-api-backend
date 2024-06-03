package icu.yeguo.yeguoapi.service.impl.provider;


import icu.yeguo.apicommon.service.DemoService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class DemoServiceImpl implements DemoService {
    @Override
    public String sayHello(String name) {
        System.out.println("DubboService_name:"+name);
        return name;
    }
}
