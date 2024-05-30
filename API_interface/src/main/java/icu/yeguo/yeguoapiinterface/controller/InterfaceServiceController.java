package icu.yeguo.yeguoapiinterface.controller;

import icu.yeguo.yeguoapisdk.client.YGAPIClient;
import icu.yeguo.yeguoapisdk.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/interface")
public class InterfaceServiceController {

    @GetMapping("test")
    public String test() {
        return "测试";
    }

    @GetMapping("getName")
    public String getNameByGet(@RequestParam("name")  String name) {
        return "GET-你的名字是：" + name;
    }

    @PostMapping("postName")
    public String getNameByPost(@RequestBody User user) {
        return "POST-你的名字是：" + user.getName();
    }

}
