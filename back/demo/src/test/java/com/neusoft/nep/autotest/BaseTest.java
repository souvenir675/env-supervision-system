package com.neusoft.nep.autotest;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * 测试基类：封装 Edge 驱动、等待、常用操作
 */
public class BaseTest {

    private static final String EDGE_DRIVER_PATH = "E:\\edgedriver\\msedgedriver.exe";

    protected static final String BASE_URL = "http://localhost:8081";

    protected EdgeDriver driver;
    protected WebDriverWait wait;

    private int screenshotIndex = 0;
    private String screenshotDir;

    protected void initDriver() {
        System.setProperty("webdriver.edge.driver", EDGE_DRIVER_PATH);

        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--start-maximized");

        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        options.addArguments("--disable-features=PasswordLeakDetection");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        driver = new EdgeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String className = this.getClass().getSimpleName();
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        screenshotDir = "screenshots" + File.separator + className + File.separator + stamp;
        try {
            Files.createDirectories(Paths.get(screenshotDir));
            System.out.println("[截图目录] " + Paths.get(screenshotDir).toAbsolutePath());
        } catch (IOException e) {
            System.err.println("创建截图目录失败: " + e.getMessage());
        }
        screenshotIndex = 0;
    }

    protected void quitDriver() {
        if (driver != null) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
            driver.quit();
        }
    }

    protected void open(String path) {
        driver.get(BASE_URL + path);
        sleep(800);
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void showMsg(String msg) {
        String script =
                "var old = document.getElementById('selenium-toast');" +
                        "if(old) old.remove();" +
                        "var d = document.createElement('div');" +
                        "d.id = 'selenium-toast';" +
                        "d.style.cssText = 'position:fixed;top:20px;right:20px;z-index:999999;" +
                        "background:#2e7d32;color:#fff;padding:16px 28px;border-radius:10px;" +
                        "font-size:20px;font-weight:bold;font-family:Microsoft YaHei;" +
                        "box-shadow:0 8px 30px rgba(46,125,50,0.5);border:3px solid #fff;';" +
                        "d.innerText = '" + msg + "';" +
                        "document.body.appendChild(d);" +
                        "setTimeout(function(){d.remove();},3000);";
        ((JavascriptExecutor) driver).executeScript(script);
        takeScreenshot(msg);
    }

    protected void screenshot(String stepName) {
        takeScreenshot(stepName);
    }

    private void takeScreenshot(String stepName) {
        if (driver == null || screenshotDir == null) return;
        screenshotIndex++;
        String safeName = (stepName == null ? "step" : stepName)
                .replaceAll("[\\\\/:*?\"<>|\\s]+", "_")
                .replaceAll("_+", "_");
        if (safeName.length() > 60) safeName = safeName.substring(0, 60);
        String fileName = String.format("%02d_%s.png", screenshotIndex, safeName);
        Path target = Paths.get(screenshotDir, fileName);
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  📸 截图: " + target.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("截图保存失败 [" + stepName + "]: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("截图失败 [" + stepName + "]: " + e.getMessage());
        }
    }

    /**
     * Element Plus 下拉框：用纯 JS 一步完成"展开+选择"
     */
    protected void selectElOption(String placeholderText, String optionText) {
        // 1. 执行 JS 展开下拉，并把结果写入页面
        String expandJs =
                "var selects = document.querySelectorAll('.el-select');" +
                        "var found = 'not found';" +
                        "for (var i = 0; i < selects.length; i++) {" +
                        "  var s = selects[i];" +
                        "  var ph = s.querySelector('.el-select__placeholder');" +
                        "  if (ph && ph.textContent.trim() === '" + placeholderText + "') {" +
                        "    var w = s.querySelector('.el-select__wrapper');" +
                        "    w.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                        "    w.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));" +
                        "    w.click();" +
                        "    found = 'expanded';" +
                        "    break;" +
                        "  }" +
                        "}" +
                        // ⭐ 把结果写进 document.title（或自定义元素）
                        "document.title = 'SELENIUM_RESULT:' + found;";

        ((JavascriptExecutor) driver).executeScript(expandJs);
        sleep(1500);

        // ⭐ 从 document.title 读回结果
        String title = driver.getTitle();
        System.out.println("页面标题：" + title);

        if (title == null || !title.contains("SELENIUM_RESULT:expanded")) {
            throw new RuntimeException("下拉展开失败，结果：" + title);
        }

        // 2. 选择选项
        String selectJs =
                "var items = document.querySelectorAll('.el-select-dropdown__item');" +
                        "var found = 'not found: ' + items.length + ' items';" +
                        "for (var i = 0; i < items.length; i++) {" +
                        "  var it = items[i];" +
                        "  if (it.textContent.trim() === '" + optionText + "') {" +
                        "    it.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                        "    it.click();" +
                        "    found = 'selected';" +
                        "    break;" +
                        "  }" +
                        "}" +
                        "document.title = 'SELENIUM_RESULT:' + found;";

        ((JavascriptExecutor) driver).executeScript(selectJs);
        sleep(800);

        String title2 = driver.getTitle();
        System.out.println("页面标题：" + title2);

        if (title2 == null || !title2.contains("SELENIUM_RESULT:selected")) {
            throw new RuntimeException("选项选择失败，结果：" + title2);
        }
    }

    /**
     * 根据用户类型获取账号输入框的 placeholder
     */
    private String getAccountPlaceholder(String userType) {
        switch (userType) {
            case "supervisor": return "请输入手机号码";
            case "grid":       return "请输入网格员登录编码";
            case "admin":      return "请输入管理员登录编号";
            case "decision":   return "请输入决策者登录编号";
            default:           return "请输入账号";
        }
    }

    /**
     * 根据用户类型获取中文标签
     */
    private String getRoleLabel(String userType) {
        switch (userType) {
            case "supervisor": return "公众监督员";
            case "grid":       return "网格员";
            case "admin":      return "管理员";
            case "decision":   return "决策者";
            default:           return userType;
        }
    }

    protected void login(String userType, String account, String password) {
        open("/login");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='password']")));
        sleep(500);

        try {
            // 1. 选角色
            WebElement radioInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("input.el-radio-button__original-radio[value='" + userType + "']")));
            WebElement label = radioInput.findElement(By.xpath("./.."));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", label);
            sleep(500);

            // 2. 账号
            WebElement accountInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[placeholder='" + getAccountPlaceholder(userType) + "']")));
            accountInput.clear();
            accountInput.sendKeys(account);

            // 3. 密码
            WebElement pwdInput = driver.findElement(By.cssSelector("input[type='password']"));
            pwdInput.clear();
            pwdInput.sendKeys(password);

            // 4. 点登录
            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.login-btn")));
            loginBtn.click();
            System.out.println("已点击登录，等待跳转...");

            // ⭐ 5. 用显式等待 URL 不再包含 /login（最多等 15 秒）
            try {
                wait.until(driver -> !driver.getCurrentUrl().contains("/login"));
                System.out.println("跳转成功，当前 URL：" + driver.getCurrentUrl());
            } catch (Exception e) {
                System.out.println("跳转超时，当前 URL：" + driver.getCurrentUrl());
                throw new RuntimeException("登录失败，URL 未跳转");
            }

            // ⭐ 6. 额外等页面渲染完成
            sleep(1500);

            showMsg("登录成功：" + getRoleLabel(userType));

        } catch (Exception e) {
            System.err.println("登录失败：");
            e.printStackTrace();
            throw new RuntimeException("登录失败: " + e.getMessage(), e);
        }
    }
}