/**
 * @author <a href="mailto:liaochunyhm@live.com">liaochuntao</a>
 * @since
 */
package com.alibaba.boot.nacos.sample;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class);
    }

    @RestController
    protected static class MyController {

        @NacosValue(value = "${people.have}", autoRefreshed = true)
        private String enable;

        @GetMapping
        public String isEnable() {
            return enable;
        }

    }

    @Configuration
    @ConditionalOnProperty(prefix = "people", name = "enable", havingValue = "true")
    protected static class People {

        @Bean
        public Object object() {
            System.out.println(Thread.currentThread().getName() + " " + getClass().getCanonicalName() + " here");
            return new Object();
        }

    }


}
