package icu.yeguo.yeguoapi;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("icu.yeguo.yeguoapi.mapper")
@EnableDubbo
@EnableTransactionManagement
public class APIBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIBackendApplication.class, args);
    }

}
