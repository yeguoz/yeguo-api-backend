package icu.yeguo.yeguoapiinterface.controller;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/")
public class InterfaceServiceController {

    @GetMapping("qq/info")
    public String getQQInfo(@RequestParam("qq") Integer qq) {
        log.info("请求到==>/api/qq/info 接口");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("qq", qq);
        return HttpUtil.get("https://api.oioweb.cn/api/qq/info", paramMap);
    }

    @GetMapping("ip/ipaddress")
    public String getIpAddress(@RequestParam("ip") String ip) {
        log.info("请求到==>/api/ip/ipaddress 接口");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ip", ip);
        return HttpUtil.get("https://api.oioweb.cn/api/ip/ipaddress", paramMap);
    }

    @GetMapping("weather")
    public String getCityWeather(@RequestParam("city_name") String cityName) {
        log.info("请求到==>/api/weather 接口");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("city_name", cityName);
        return HttpUtil.get("https://api.oioweb.cn/api/weather/weather", paramMap);
    }

    @GetMapping("phone")
    public String getPhoneLocation(@RequestParam("number") String number) {
        log.info("请求到==>/api/phone 接口");
        log.info("拼接地址为："+"https://api.vvhan.com/api/phone"+"/"+number);
        return HttpUtil.get("https://api.vvhan.com/api/phone"+"/"+number);
    }

}
