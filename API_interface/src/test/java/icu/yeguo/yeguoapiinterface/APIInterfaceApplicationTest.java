//package icu.yeguo.yeguoapiinterface;
//
//import icu.yeguo.yeguoapisdk.client.YGAPIClient;
//import icu.yeguo.yeguoapisdk.model.User;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class APIInterfaceApplicationTest {
//
//    @Autowired
//    private YGAPIClient ygapiClient;
//
//    @Test
//    public void test1() {
//        ygapiClient.getTest();
//
//        ygapiClient.getNameByGet("jsp");
//        User user = new User("野果");
//        ygapiClient.getNameByPost(user);
//        System.out.println("==============");
//        System.out.println(ygapiClient.getAccessKey());
//        System.out.println(ygapiClient.getSecretKey());
//    }
//}
