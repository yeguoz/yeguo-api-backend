package icu.yeguo.yeguoapiinterface.controller;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/")
public class InterfaceServiceController {
    // 不能使用了
    @GetMapping("qq/info")
    public String getQQInfo(@RequestParam("qq") Long qq) {
        log.info("请求到==>/api/qq/info接口==qq:" + qq);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("qq", qq);
        return HttpUtil.get("https://api.oioweb.cn/api/qq/info", paramMap);
    }

    // 获取ip地址
    @GetMapping("ip/ipaddress")
    public String getIpAddress(@RequestParam("ip") String ip) {
        log.info("请求到==>/api/ip/ipaddress接口==ip" + ip);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ip", ip);
        return HttpUtil.get("https://api.oioweb.cn/api/ip/ipaddress", paramMap);
    }

    // 获取天气
    @GetMapping("weather")
    public String getCityWeather(@RequestParam("city_name") String cityName) {
        log.info("请求到==>/api/weather接口==cityName" + cityName);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("city_name", cityName);
        return HttpUtil.get("https://api.oioweb.cn/api/weather/weather", paramMap);
    }

    // 获取手机归属地
    @GetMapping("common/teladress")
    public String getPhoneLocation(@RequestParam("mobile") String mobile) {
        log.info("请求到==>/api/phone接口==mobile:" + mobile);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("mobile", mobile);
        return HttpUtil.get("https://api.oioweb.cn/api/common/teladress", paramMap);
    }

    // 获取网站备案信息
    @GetMapping("site/icp")
    public String getSiteIcp(@RequestParam("domain") String domain) {
        log.info("请求到==>/api/site/icp接口==domain:" + domain);
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("domain", domain);
        return HttpUtil.get("https://api.oioweb.cn/api/site/icp", paramMap);
    }

    // 二维码生成
    @GetMapping("qrcode/encode")
    public String getEncode(@RequestParam("text") String text,
                            @RequestParam(value = "m", required = false) Integer m,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "size", required = false) Integer size) {
        log.info("请求到==>/api/qrcode/encode接口==text:" + text+",m:"+m+",type:"+type+",size:"+size);
        // 如果参数为空，设置默认值
        if (m == null) {
            m = 2;
        }
        if (type == null) {
            type = "svg";
        }
        if (size == null) {
            size = 15;
        }
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("text", text);
        paramMap.put("m", m);
        paramMap.put("type", type);
        paramMap.put("size", size);
        return HttpUtil.get("https://api.oioweb.cn/api/qrcode/encode", paramMap);
    }
}
