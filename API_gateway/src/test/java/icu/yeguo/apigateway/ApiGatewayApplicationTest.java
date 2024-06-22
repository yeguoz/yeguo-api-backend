package icu.yeguo.apigateway;

import icu.yeguo.apicommon.service.CommonService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ApiGatewayApplication.class)
public class ApiGatewayApplicationTest {

    @DubboReference
    private CommonService commonService;

    @Test
    public void test1() {
        String s = commonService.sayHello("jsp");
        System.out.println("Test:"+s);

    }
}
