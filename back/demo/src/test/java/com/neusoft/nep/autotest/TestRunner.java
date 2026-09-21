package com.neusoft.nep.autotest;

/**
 * 测试入口：依次执行所有角色的自动化测试
 */
public class TestRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("========== 开始自动化测试 ==========");

        System.out.println("\n>>> 1. 监督员流程测试");
        runTest(SupervisorTest.class);

        System.out.println("\n>>> 2. 网格员流程测试");
        runTest(GridMemberTest.class);

        System.out.println("\n>>> 3. 管理员流程测试");
        runTest(AdminTest.class);

        System.out.println("\n========== 测试全部完成 ==========");
    }

    private static void runTest(Class<?> clazz) throws Exception {
        Object test = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("setUp").invoke(test);

        for (java.lang.reflect.Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
                try {
                    method.invoke(test);
                } catch (Exception e) {
                    System.err.println("❌ 测试失败: " + method.getName());
                    // ⭐ 打印真实异常（去掉 InvocationTargetException 包装）
                    Throwable realCause = e;
                    if (e instanceof java.lang.reflect.InvocationTargetException && e.getCause() != null) {
                        realCause = e.getCause();
                    }
                    realCause.printStackTrace();
                }
            }
        }

        clazz.getMethod("tearDown").invoke(test);
    }
}