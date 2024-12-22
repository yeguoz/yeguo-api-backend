package icu.yeguo.yeguoapiinterface.controller;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import icu.yeguo.yeguoapiinterface.common.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/")
public class InterfaceServiceController {
    // 1. qq 信息
    @GetMapping("qq/info")
    public String getQQInfo(@RequestParam("qq") Long qq) {
        log.info("请求到==>/api/app/qq/info接口==qq:{}", qq);
        String result = executeGetRequest("https://api.uomg.com/api/qq.info", "qq", qq);
        log.info("qq/info:{}", result);
        return result;
    }

    // 2.获取ip地址
    @GetMapping("ipaddress")
    public String getIpAddress(@RequestParam(value = "ip", required = false) String ip) {
        log.info("请求到==>/api/app/ipaddress接口==ip:{}", ip);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("json", "true");
        paramMap.put("ip", ip);
        String result = HttpUtil.get("http://whois.pconline.com.cn/ipJson.jsp", paramMap);
        log.info("ip地址:{}", result);
        if (result.contains("503"))
            return JSONUtil.toJsonStr(new Response<>(503, "", "接口不稳定请稍后再试"));
        return result.strip();
    }

    // 3.获取天气
    @GetMapping("weather")
    public String getCityWeather(@RequestParam("city") String city) {
        log.info("请求到==>/api/app/weather接口==city:{}", city);
        String result = executeGetRequest("http://shanhe.kim/api/za/tianqi.php", "city", city);
        log.info("城市天气：{}", result);
        return result;
    }

    // 4.获取手机归属地
    @GetMapping("phone/location")
    public String getPhoneLocation(@RequestParam("tel") String tel) {
        log.info("请求到==>/api/app/phone/location接口==mobile:{}", tel);
        String result = executeGetRequest("http://shanhe.kim/api/za/phone.php", "tel", tel);
        log.info("手机归属地：{}", result);
        return result;
    }


    // 5.二维码解析
    @GetMapping("qrcode/decode")
    public String getQrcodeDecode(@RequestParam("url") String url) {
        log.info("请求到==>/api/app/qrcode/decode接口==url:{}", url);
        String result = executeGetRequest("https://api.uomg.com/api/qr.encode", "url", url);
        log.info("二维码解析：{}", result);
        return result;
    }

    // 6.微博热搜
    @GetMapping("weibo/hot")
    public String getWBHotSearch() {
        log.info("请求到==>/api/app/weibo/hot接口");
        String result = HttpUtil.get("http://shanhe.kim/api/za/weibo.php");
        log.info("微博热搜：{}", result);
        return result;
    }

    // 7.哔哩哔哩热搜
    @GetMapping("bilibili/hot")
    public String getBilibiliHotSearch() {
        log.info("请求到==>/api/app/bilibili/hot接口");
        String result = HttpUtil.get("https://api.vvhan.com/api/hotlist/bili");
        log.info("哔哩哔哩热搜：{}", result);
        return result;
    }

    // 8.每日英语
    @GetMapping("daily/english")
    public String getDailyEnglish(@RequestParam(value = "type", required = false) String type) {
        log.info("请求到==>/api/app/daily/english接口");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", type);
        String result = HttpUtil.get("https://api.vvhan.com/api/dailyEnglish", paramMap);
        log.info("每日英语：{}", result);
        return result;
    }

    // 9. IT资讯热榜
    @GetMapping("it/hot")
    public String getITHotSearch() {
        log.info("请求到==>/api/app/it/hot接口");
        String result = HttpUtil.get("https://api.vvhan.com/api/hotlist/itNews");
        log.info("IT热搜：{}", result);
        return result;
    }

    // 10. 星座运势
    @GetMapping("horoscope")
    public String getHoroscope(@RequestParam("type") String type, @RequestParam("time") String time) {
        log.info("请求到==>/api/app/horoscope接口");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", type);
        paramMap.put("time", time);
        String result = HttpUtil.get("https://api.vvhan.com/api/horoscope", paramMap);
        log.info("星座运势：{}", result);
        return result;
    }

    private <T> String executeGetRequest(String url, String paramName, T paramValue) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(paramName, paramValue);
        return HttpUtil.get(url, paramMap);
    }

    // 处理上传的文件发起请求
    private String handleFileUpload(MultipartFile file, String url) {
        File tempFile;
        try {
            tempFile = File.createTempFile("upload", file.getOriginalFilename());
            file.transferTo(tempFile);
        } catch (IOException e) {
            log.error("转换文件失败", e);
            return new Response<>(500, null, "转换文件失败!").toString();
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("file", tempFile);

        String result = HttpUtil.post(url, paramMap);

        if (tempFile.exists()) {
            boolean isDeleted = tempFile.delete();
            if (isDeleted) {
                log.info("File deleted successfully");
            } else {
                log.error("Failed to delete file");
            }
        }
        return result;
    }
}
