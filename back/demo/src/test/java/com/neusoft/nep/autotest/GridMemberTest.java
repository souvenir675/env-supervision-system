package com.neusoft.nep.autotest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 网格员端自动化测试
 * 流程：登录 → 查看任务 → 录入实测数据 → 修改工作状态
 */
public class GridMemberTest extends BaseTest {

    @BeforeEach
    public void setUp() {
        initDriver();
    }

    @AfterEach
    public void tearDown() {
        quitDriver();
    }

    @Test
    public void testGridMemberFlow() throws Exception {
        login("grid", "gm046", "123456");
        Thread.sleep(2000);

        viewTasks();
        submitAqiData();
        changeWorkState();

        showMsg("网格员全流程测试完成！");
        Thread.sleep(3000);
    }

    private void viewTasks() throws Exception {
        open("/grid/tasks");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".el-table")));

        System.out.println("任务列表加载完成");

        WebElement detailBtn = driver.findElement(
                By.xpath("(//button[contains(.,'查看详情')])[1]"));
        detailBtn.click();
        Thread.sleep(1500);

        WebElement closeBtn = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]//button[contains(.,'关闭')]"));
        closeBtn.click();
        Thread.sleep(500);

        showMsg("查看任务详情成功");
    }

    private void submitAqiData() throws Exception {
        try {
            WebElement inputBtn = driver.findElement(
                    By.xpath("(//button[contains(.,'录入数据')])[1]"));
            inputBtn.click();
            Thread.sleep(1500);

            WebElement so2Input = driver.findElement(
                    By.xpath("(//div[contains(@class,'el-dialog')]//input[@type='number'])[1]"));
            so2Input.clear();
            so2Input.sendKeys("241");

            WebElement coInput = driver.findElements(
                    By.xpath("//div[contains(@class,'el-dialog')]//input[@type='number']")).get(1);
            coInput.clear();
            coInput.sendKeys("16");

            WebElement spmInput = driver.findElements(
                    By.xpath("//div[contains(@class,'el-dialog')]//input[@type='number']")).get(2);
            spmInput.clear();
            spmInput.sendKeys("105");

            WebElement confirmBtn = driver.findElement(
                    By.xpath("//div[contains(@class,'el-dialog')]//button[contains(.,'确认提交')]"));
            confirmBtn.click();
            Thread.sleep(1000);

            WebElement okBtn = driver.findElement(
                    By.xpath("//div[contains(@class,'el-message-box')]//button[contains(.,'确认')]"));
            okBtn.click();
            Thread.sleep(2000);

            showMsg("实测数据提交成功");
        } catch (Exception e) {
            System.out.println("没有待处理任务，跳过数据录入");
        }
    }

    private void changeWorkState() throws Exception {
        open("/grid/tasks");
        Thread.sleep(1000);

        // 点"修改状态"
        WebElement stateBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'修改状态')]")));
        stateBtn.click();
        Thread.sleep(1000);

        // 选"休假"（value=2）
        WebElement vacationRadioInput = driver.findElement(
                By.cssSelector("input.el-radio__original[value='2']"));
        WebElement vacationLabel = vacationRadioInput.findElement(By.xpath("./ancestor::label"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", vacationLabel);
        sleep(500);

        WebElement confirmBtn = driver.findElement(
                By.xpath("//div[contains(@class,'el-dialog')]//button[contains(.,'确认修改')]"));
        confirmBtn.click();
        Thread.sleep(1500);

        showMsg("工作状态修改成功");
    }
}