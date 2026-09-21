package com.neusoft.nep;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring Boot 上下文加载测试
 * 仅用于验证 Spring 容器能否启动，不涉及业务逻辑
 */
@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
        // 只验证 Spring 容器能否加载
        // 如果启动失败会抛异常，测试就失败
    }
}