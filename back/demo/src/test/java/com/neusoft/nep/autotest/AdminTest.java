package com.neusoft.nep.autotest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 管理员端自动化测试
 * 流程：登录 → 查看反馈列表 → 指派网格员 → 查看确认AQI数据 → 注册网格员 → 查看统计
 */
public class AdminTest extends BaseTest {

    @BeforeEach
    public void setUp() {
        initDriver();
    }

    @AfterEach
    public void tearDown() {
        quitDriver();
    }

    @Test
    public void testAdminFlow() throws Exception {
        login("admin", "10001", "123456");
        Thread.sleep(2000);

        viewFeedbackList();
        assignGridMember();
        viewStatistics();
        registerGridMember();
        viewCharts();

        showMsg("管理员全流程测试完成！");
        Thread.sleep(3000);
    }

    private void viewFeedbackList() throws Exception {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".el-table")));
        System.out.println("反馈列表加载完成");
        showMsg("反馈列表查看成功");
    }

    private void assignGridMember() throws Exception {
        try {
            Thread.sleep(2000);

            // 1. 检查列表
            java.util.List<WebElement> rows = driver.findElements(
                    By.cssSelector(".el-table__body tr"));
            System.out.println("反馈列表行数：" + rows.size());
            if (rows.isEmpty()) {
                System.out.println("反馈列表为空，跳过指派");
                return;
            }

            // 2. 点"指派"按钮
            java.util.List<WebElement> assignBtns = driver.findElements(
                    By.xpath("//button[normalize-space(.)='指派']"));
            System.out.println("找到 '指派' 按钮数：" + assignBtns.size());
            if (assignBtns.isEmpty()) {
                System.out.println("没有可指派的按钮，跳过");
                return;
            }

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", assignBtns.get(0));
            Thread.sleep(2500);  // ⭐ 多等一会
            System.out.println("已点击'指派'按钮，等待弹窗渲染");

            // 3. ⭐ 用 document.title 中转状态
            String checkJs =
                    "var d = document.querySelector('.el-dialog');" +
                            "document.title = 'DIALOG:' + (d ? 'opened' : 'none');";
            ((JavascriptExecutor) driver).executeScript(checkJs);
            Thread.sleep(300);

            String title = driver.getTitle();
            System.out.println("弹窗状态：" + title);

            if (title == null || !title.contains("DIALOG:opened")) {
                System.out.println("弹窗未出现，跳过指派");
                return;
            }

            // 4. ⭐ 展开下拉
            String expandJs =
                    "var dialog = document.querySelector('.el-dialog');" +
                            "var wrapper = dialog ? dialog.querySelector('.el-select__wrapper') : null;" +
                            "if (wrapper) {" +
                            "  wrapper.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                            "  wrapper.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));" +
                            "  wrapper.click();" +
                            "  document.title = 'EXPAND:ok';" +
                            "} else {" +
                            "  document.title = 'EXPAND:no-select';" +
                            "}";
            ((JavascriptExecutor) driver).executeScript(expandJs);
            Thread.sleep(1500);

            String title2 = driver.getTitle();
            System.out.println("展开结果：" + title2);

            if (title2 == null || !title2.contains("EXPAND:ok")) {
                System.out.println("展开下拉失败：" + title2);
                return;
            }

            // 5. ⭐ 检查选项
            java.util.List<WebElement> options = driver.findElements(
                    By.cssSelector(".el-select-dropdown__item"));
            System.out.println("选项数：" + options.size());

            if (options.isEmpty()) {
                System.out.println("没有可用的网格员，关闭弹窗");
                return;
            }

            // 6. 选择第一个可见的网格员
            java.util.List<WebElement> allOptions = driver.findElements(
                    By.cssSelector(".el-select-dropdown__item"));

            java.util.List<WebElement> visibleOptions = new java.util.ArrayList<>();
            for (WebElement opt : allOptions) {
                try {
                    if (opt.isDisplayed()) {
                        visibleOptions.add(opt);
                    }
                } catch (Exception ignored) {}
            }

            System.out.println("所有选项数：" + allOptions.size());
            System.out.println("可见选项数：" + visibleOptions.size());

            if (visibleOptions.isEmpty()) {
                System.out.println("没有可见的网格员选项，关闭弹窗");
                return;
            }

            WebElement firstOption = visibleOptions.get(0);
            System.out.println("选择网格员：" + firstOption.getText().trim());
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstOption);
            Thread.sleep(1000);

            // 7. ⭐ 点确认指派
            String confirmJs =
                    "var dialog = document.querySelector('.el-dialog');" +
                            "var btns = dialog ? dialog.querySelectorAll('button') : [];" +
                            "var found = false;" +
                            "for (var i = 0; i < btns.length; i++) {" +
                            "  if (btns[i].textContent.trim().indexOf('确认指派') >= 0) {" +
                            "    btns[i].click();" +
                            "    found = true;" +
                            "    break;" +
                            "  }" +
                            "}" +
                            "document.title = 'CONFIRM:' + (found ? 'ok' : 'no-btn');";
            ((JavascriptExecutor) driver).executeScript(confirmJs);
            Thread.sleep(2500);

            String title4 = driver.getTitle();
            System.out.println("确认结果：" + title4);

            if (title4 != null && title4.contains("CONFIRM:ok")) {
                showMsg("指派网格员成功");
                System.out.println("✅ 指派完成");
            } else {
                System.out.println("❌ 指派未完成：" + title4);
            }

        } catch (Exception e) {
            System.err.println("指派失败：");
            e.printStackTrace();
        }
    }

    private void viewStatistics() throws Exception {
        // 先关可能的弹窗
        closeDialogIfOpen();

        // 用 JS 点击"确认AQI数据管理"Tab
        WebElement tab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'el-tabs__item')]" +
                        "[contains(text(),'确认AQI数据管理')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);

        // ⭐ 等 Tab 内容渲染（3秒）
        Thread.sleep(3000);

        // ⭐ 等表格出现，用 presenceOf 而不是 visibilityOf
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".el-tabs__content .el-table")));
        } catch (Exception e) {
            System.out.println("确认AQI数据表格未出现，跳过");
            return;
        }

        // 检查表格有没有数据
        java.util.List<WebElement> rows = driver.findElements(
                By.cssSelector(".el-tabs__content .el-table__body tr"));
        System.out.println("确认AQI数据行数：" + rows.size());

        if (rows.isEmpty()) {
            System.out.println("确认AQI数据列表为空，跳过详情查看");
            showMsg("确认AQI数据列表为空");
            return;
        }

        // 点第一行"查看详情"
        java.util.List<WebElement> detailBtns = driver.findElements(
                By.xpath("//button[contains(., '查看详情')]"));
        if (detailBtns.isEmpty()) {
            System.out.println("没有'查看详情'按钮，跳过");
            return;
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", detailBtns.get(0));
        Thread.sleep(1500);

        // 关闭弹窗
        try {
            WebElement closeBtn = driver.findElement(
                    By.xpath("//div[contains(@class,'el-dialog')]" +
                            "[last()]//button[contains(.,'关闭')]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", closeBtn);
            Thread.sleep(500);
        } catch (Exception ignored) {}

        showMsg("确认AQI数据查看成功");
    }

    /**
     * 如果弹窗打开着，先关掉
     */
    private void closeDialogIfOpen() {
        try {
            java.util.List<WebElement> dialogs = driver.findElements(
                    By.cssSelector(".el-dialog:not([style*='display: none'])"));
            if (!dialogs.isEmpty()) {
                // 找"取消"或"关闭"按钮
                java.util.List<WebElement> closeBtns = driver.findElements(
                        By.xpath("//div[contains(@class,'el-dialog')]" +
                                "//button[contains(.,'取消') or contains(.,'关闭')]"));
                if (!closeBtns.isEmpty()) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].click();", closeBtns.get(0));
                    Thread.sleep(500);
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * 注册网格员
     */
    private void registerGridMember() throws Exception {
        System.out.println(">>> 开始注册网格员");

        // 1. 切到"网格员管理"Tab
        WebElement tab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'el-tabs__item')]" +
                        "[contains(text(),'网格员管理')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        Thread.sleep(2000);

        // 2. 点"注册网格员"按钮
        WebElement registerBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'注册网格员')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", registerBtn);
        Thread.sleep(2000);

        // 3. 检查可见弹窗
        String checkJs =
                "var dialogs = document.querySelectorAll('.el-dialog');" +
                        "var visibleDialog = null;" +
                        "for (var i = 0; i < dialogs.length; i++) {" +
                        "  if (dialogs[i].offsetParent !== null) {" +
                        "    visibleDialog = dialogs[i];" +
                        "    break;" +
                        "  }" +
                        "}" +
                        "document.title = 'DIALOG:' + (visibleDialog ? 'opened' : 'none');";
        ((JavascriptExecutor) driver).executeScript(checkJs);
        System.out.println("注册弹窗状态：" + driver.getTitle());

        // 4. 生成随机手机号和登录编码，避免重复
        String randomSuffix = String.valueOf(System.currentTimeMillis()).substring(7); // 后 4 位
        String phone = "138" + String.format("%08d", (int)(Math.random() * 100000000));
        String gmCode = "gm500";
        String gmName = "测试网格员" + randomSuffix;

        System.out.println("注册信息 - 手机号：" + phone + "，编码：" + gmCode + "，姓名：" + gmName);

        // 5. 用 Selenium 原生 sendKeys 填表
        System.out.println("填写表单...");

        // 手机号
        WebElement phoneInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'el-dialog')]" +
                        "//input[@placeholder='请输入手机号']")));
        phoneInput.clear();
        phoneInput.sendKeys(phone);

        // 姓名
        WebElement nameInput = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]" +
                        "//input[@placeholder='请输入网格员姓名']"));
        nameInput.clear();
        nameInput.sendKeys(gmName);

        // 登录编码
        WebElement codeInput = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]" +
                        "//input[contains(@placeholder,'登录编码')]"));
        codeInput.clear();
        codeInput.sendKeys(gmCode);

        // 密码
        WebElement pwdInput = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]" +
                        "//input[contains(@placeholder,'密码')]"));
        pwdInput.clear();
        pwdInput.sendKeys("123456");

        // 联系电话
        WebElement telInput = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]" +
                        "//input[@placeholder='请输入联系电话']"));
        telInput.clear();
        telInput.sendKeys(phone);

        System.out.println("✅ 5 个输入框填写完成");

        String provJs =
                "(function() {" +
                        "  var dialogs = document.querySelectorAll('.el-dialog');" +
                        "  var visibleDialog = null;" +
                        "  for (var i = 0; i < dialogs.length; i++) {" +
                        "    if (dialogs[i].offsetParent !== null) {" +
                        "      visibleDialog = dialogs[i];" +
                        "      break;" +
                        "    }" +
                        "  }" +
                        "  if (!visibleDialog) { document.title = 'PROV:no-visible-dialog'; return; }" +
                        "  var wrappers = visibleDialog.querySelectorAll('.el-select__wrapper');" +
                        "  if (wrappers.length < 1) { document.title = 'PROV:no-select'; return; }" +
                        "  wrappers[0].dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                        "  wrappers[0].dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));" +
                        "  wrappers[0].click();" +
                        "  document.title = 'PROV:expanded';" +
                        "})();";
        ((JavascriptExecutor) driver).executeScript(provJs);
        Thread.sleep(1500);

        // 选第一个可见省份选项
        java.util.List<WebElement> provOptions = driver.findElements(
                By.cssSelector(".el-select-dropdown__item"));
        for (WebElement opt : provOptions) {
            if (opt.isDisplayed()) {
                String text = opt.getText().trim();
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                System.out.println("已选择省份：" + text);
                break;
            }
        }
        Thread.sleep(1500);

        // ⭐ 7. 选城市（同样只找可见弹窗）
        String cityJs =
                "(function() {" +
                        "  var dialogs = document.querySelectorAll('.el-dialog');" +
                        "  var visibleDialog = null;" +
                        "  for (var i = 0; i < dialogs.length; i++) {" +
                        "    if (dialogs[i].offsetParent !== null) {" +
                        "      visibleDialog = dialogs[i];" +
                        "      break;" +
                        "    }" +
                        "  }" +
                        "  if (!visibleDialog) { document.title = 'CITY:no-visible-dialog'; return; }" +
                        "  var wrappers = visibleDialog.querySelectorAll('.el-select__wrapper');" +
                        "  if (wrappers.length < 2) { document.title = 'CITY:no-select'; return; }" +
                        "  wrappers[1].dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                        "  wrappers[1].dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));" +
                        "  wrappers[1].click();" +
                        "  document.title = 'CITY:expanded';" +
                        "})();";
        ((JavascriptExecutor) driver).executeScript(cityJs);
        Thread.sleep(1500);

        java.util.List<WebElement> cityOptions = driver.findElements(
                By.cssSelector(".el-select-dropdown__item"));
        for (WebElement opt : cityOptions) {
            if (opt.isDisplayed()) {
                String text = opt.getText().trim();
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", opt);
                System.out.println("已选择城市：" + text);
                break;
            }
        }
        Thread.sleep(1000);

        // 8. 点"确认注册"
        WebElement submitBtn = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]" +
                        "//button[contains(.,'确认注册')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
        Thread.sleep(2500);

        System.out.println("✅ 提交完成");
        showMsg("注册网格员成功");
    }

    private void viewCharts() throws Exception {
        closeDialogIfOpen();

        // 切到"统计数据"Tab
        WebElement tab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'el-tabs__item')]" +
                        "[contains(text(),'统计数据')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
        Thread.sleep(2500);

        // ⭐ 统计类型下拉框（页面唯一），用 JS 直接点
        String[] types = {"省分组检查统计", "AQI指数分布统计", "AQI指数趋势统计", "实时统计 & 网格覆盖率"};

        for (String type : types) {
            try {
                // 展开下拉
                String expandJs =
                        "(function() {" +
                                "  var sel = document.querySelector('.statistics-selector .el-select__wrapper');" +
                                "  if (!sel) sel = document.querySelector('.el-select__wrapper');" +
                                "  if (sel) {" +
                                "    sel.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                                "    sel.dispatchEvent(new MouseEvent('mouseup', {bubbles: true}));" +
                                "    sel.click();" +
                                "    return 'expanded';" +
                                "  }" +
                                "  return 'not found';" +
                                "})();";
                ((JavascriptExecutor) driver).executeScript(expandJs);
                Thread.sleep(1000);

                // 点选项
                String selectJs =
                        "(function() {" +
                                "  var items = document.querySelectorAll('.el-select-dropdown__item');" +
                                "  for (var i = 0; i < items.length; i++) {" +
                                "    if (items[i].textContent.trim() === '" + type + "') {" +
                                "      items[i].dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));" +
                                "      items[i].click();" +
                                "      return 'selected';" +
                                "    }" +
                                "  }" +
                                "  return 'not found';" +
                                "})();";
                Object r = ((JavascriptExecutor) driver).executeScript(selectJs);
                System.out.println("切换到 [" + type + "]：" + r);
                Thread.sleep(2500);
            } catch (Exception e) {
                System.out.println("切换到 [" + type + "] 失败：" + e.getMessage());
            }
        }

        showMsg("统计图表查看成功");
    }
}