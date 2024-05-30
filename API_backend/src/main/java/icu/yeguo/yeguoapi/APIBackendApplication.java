package icu.yeguo.yeguoapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("icu.yeguo.yeguoapi.mapper")
public class APIBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIBackendApplication.class, args);
    }

}
