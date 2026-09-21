package com.neusoft.nep.autotest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 监督员端自动化测试
 * 流程：登录 → 提交反馈 → 查看反馈历史
 */
public class SupervisorTest extends BaseTest {

    @BeforeEach
    public void setUp() {
        initDriver();
    }

    @AfterEach
    public void tearDown() {
        quitDriver();
    }

    @Test
    public void testSupervisorFlow() throws Exception {
        login("supervisor", "13352055098", "nidemingzi1");
        Thread.sleep(2000);

        submitFeedback();
        viewHistory();

        showMsg("监督员全流程测试完成！");
        Thread.sleep(3000);
    }

    private void submitFeedback() throws Exception {
        open("/feedback/submit");

        // 1. 选省份（新方法，传 placeholder 文字）
        selectElOption("请选择省份", "河北省");
        Thread.sleep(2000);

        // 2. 选城市
        selectElOption("请选择城市", "保定市");
        Thread.sleep(1000);

        WebElement addressInput = driver.findElement(
                By.cssSelector("input[placeholder*='请输入您观测的具体地址']"));
        addressInput.clear();
        addressInput.sendKeys("长宁南里");


        // 4. 预估AQI等级（el-radio-button，用 value 定位）
        WebElement gradeRadio = driver.findElement(
                By.cssSelector("input.el-radio-button__original-radio[value='3']"));
        WebElement gradeLabel = gradeRadio.findElement(By.xpath("./.."));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", gradeLabel);
        Thread.sleep(300);

        WebElement infoArea = driver.findElement(
                By.cssSelector("textarea[placeholder*='请描述您观察到的空气质量情况']"));
        infoArea.clear();
        infoArea.sendKeys("空气不太清新。");

        WebElement submitBtn = driver.findElement(
                By.xpath("//button[contains(.,'提交反馈')]"));
        submitBtn.click();
        Thread.sleep(2000);

        System.out.println("反馈提交完成");
        showMsg("反馈提交成功");
    }

    private void viewHistory() throws Exception {
        open("/feedback/history");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".el-table")));

        System.out.println("历史记录加载完成");

        WebElement viewBtn = driver.findElement(
                By.xpath("(//button[contains(.,'查看')])[1]"));
        viewBtn.click();
        Thread.sleep(1500);

        System.out.println("反馈详情已打开");
        showMsg("查看反馈历史成功");
        Thread.sleep(2000);
    }
}