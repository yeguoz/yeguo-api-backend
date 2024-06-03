package icu.yeguo.apigateway;

import icu.yeguo.apicommon.service.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ApiGatewayApplication.class)
public class ApiGatewayApplicationTest {

    @DubboReference
    private DemoService demoService;

    @Test
    public void test1() {
        String s = demoService.sayHello("jsp");
        System.out.println("Test:"+s);

    }
}
