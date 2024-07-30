package icu.yeguo.yeguoapiinterface.controller;

import cn.hutool.http.HttpUtil;
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
    // 不能使用了
    @GetMapping("qq/info")
    public String getQQInfo(@RequestParam("qq") Long qq) {
        log.info("请求到==>/api/qq/info接口==qq:" + qq);
        return executeGetRequest("https://api.oioweb.cn/api/qq/info","qq",qq);
    }

    // 1.获取ip地址
    @GetMapping("ip/ipaddress")
    public String getIpAddress(@RequestParam("ip") String ip) {
        log.info("请求到==>/api/ip/ipaddress接口==ip" + ip);
        return executeGetRequest("https://api.oioweb.cn/api/ip/ipaddress","ip",ip);
    }

    // 2.获取天气
    @GetMapping("weather")
    public String getCityWeather(@RequestParam("city_name") String cityName) {
        log.info("请求到==>/api/weather接口==cityName" + cityName);
        return executeGetRequest("https://api.oioweb.cn/api/weather/weather","city_name",cityName);
    }

    // 3.获取手机归属地
    @GetMapping("common/teladress")
    public String getPhoneLocation(@RequestParam("mobile") String mobile) {
        log.info("请求到==>/api/phone接口==mobile:" + mobile);
        return executeGetRequest("https://api.oioweb.cn/api/common/teladress","mobile",mobile);
    }

    // 4.获取网站备案信息
    @GetMapping("site/icp")
    public String getSiteIcp(@RequestParam("domain") String domain) {
        log.info("请求到==>/api/site/icp接口==domain:" + domain);
        return executeGetRequest("https://api.oioweb.cn/api/site/icp","domain",domain);
    }

    // 5.二维码生成
    @GetMapping("qrcode/encode")
    public String getQrcodeEncode(@RequestParam("text") String text,
                            @RequestParam(value = "m", required = false) Integer m,
                            @RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "size", required = false) Integer size) {
        log.info("请求到==>/api/qrcode/encode接口==text:" + text + ",m:" + m + ",type:" + type + ",size:" + size);
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

    // 6.二维码解析
    @PostMapping("qrcode/decode")
    public String getQrcodeDecode(@RequestPart("file") MultipartFile file) {
        log.info("请求到==>/api/qrcode/decode接口==file");
        return handleFileUpload(file, "https://api.oioweb.cn/api/qrcode/decode");
    }

    // 7.每日电影
    @GetMapping("common/OneFilm")
    public String getOneFilm() {
        log.info("请求到==>/api/common/OneFilm接口");
        return HttpUtil.get("https://api.oioweb.cn/api/common/OneFilm");
    }

    // 8.看图识物
    @PostMapping("ocr/recognition")
    public String recognition(@RequestPart("file") MultipartFile file) {
        log.info("请求到==>/api/ocr/recognition接口==file");
        return handleFileUpload(file, "https://api.oioweb.cn/api/ocr/recognition");
    }

    // 9.以图识番
    @PostMapping("search/anilistInfo")
    public String searchAnimeInfo(@RequestPart("file") MultipartFile file) {
        log.info("请求到==>/api/search/anilistInfo接口==file");
        return handleFileUpload(file, "https://api.oioweb.cn/api/search/anilistInfo");
    }

    // 10.日读世界60s
    @GetMapping("common/today")
    public String getTodayInfo() {
        log.info("请求到==>/api/common/today接口");
        return HttpUtil.get("https://api.oioweb.cn/api/common/today");
    }

    // 11.热搜榜
    @GetMapping("common/fetchHotSearchBoard")
    public String getHotSearch(@RequestParam("type") String type) {
        log.info("请求到==>/api/common/fetchHotSearchBoard接口");
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", type);
        return HttpUtil.get("https://api.oioweb.cn/api/common/fetchHotSearchBoard",paramMap);
    }

    private <T> String executeGetRequest(String url, String paramName, T paramValue) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(paramName, paramValue);
        return HttpUtil.get(url, paramMap);
    }

    private String handleFileUpload(MultipartFile file, String url) {
        File tempFile;
        try {
            tempFile = File.createTempFile("upload", file.getOriginalFilename());
            file.transferTo(tempFile);
        } catch (IOException e) {
            log.error("转换文件失败", e);
            return "{\"code\":500,\"result\":null,\"msg\":\"转换文件失败!\"}";
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
