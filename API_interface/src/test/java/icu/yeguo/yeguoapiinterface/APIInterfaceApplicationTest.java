package icu.yeguo.yeguoapiinterface;

import icu.yeguo.yeguoapisdk.client.YGApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class APIInterfaceApplicationTest {

    @Autowired
    private YGApiClient ygapiClient;

    @Test
    public void test1() {
        String result1 = ygapiClient.getIpAddress("111.56.36.134");
        String result2 = ygapiClient.getCityWeather("洛阳");
        String result3 = ygapiClient.getPhoneLocation("17337904072");
        String result4 = ygapiClient.getSiteIcp("bilibili.com");
        String result5 = ygapiClient.getQrcodeEncode("https://bilibili.com");
        String result6 = ygapiClient.getQrcodeDecode("C:\\Users\\Lenovo\\Pictures\\zfbskm.jpg");
        String result7 = ygapiClient.getOneFilm();
        String result8 = ygapiClient.recognition("C:\\Users\\Lenovo\\Pictures\\zfbskm.jpg");
        String result9 = ygapiClient.searchAnimeInfo("C:\\Users\\Lenovo\\Pictures\\api1.jpg");
        String result10 = ygapiClient.getTodayInfo();
        String result11 = ygapiClient.getHotSearch("baidu");

        System.out.println("getIpAddress:" + result1);
        System.out.println("getCityWeather:" + result2);
        System.out.println("getPhoneLocation:" + result3);
        System.out.println("getSiteIcp:" + result4);
        System.out.println("getQrcodeEncode:" + result5);
        System.out.println("getQrcodeDecode:" + result6);
        System.out.println("getOneFilm:" + result7);
        System.out.println("recognition:" + result8);
        System.out.println("searchAnimeInfo:" + result9);
        System.out.println("getTodayInfo:" + result10);
        System.out.println("getHotSearch:" + result11);
    }
}
