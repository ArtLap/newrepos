import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class Tests {
    public WebDriver browser = Browser.getDriver();

    @BeforeClass
    public void init(){
        browser.manage().window().maximize();
        browser.get("https://google.com.ua");
    }

    @Test(priority = 0)
    public void test1(){
        String gValue = browser.findElement(By.cssSelector("input[name=btnI")).getAttribute("value");
        Assert.assertEquals(gValue, "Мне повезёт!", "Results are not matched");
    }

    @Test(priority = 5)
    public void test2(){
        WebElement search = browser.findElement(By.cssSelector("input[type=text]"));
        search.sendKeys("I`m lucky one");
        search.sendKeys(Keys.ENTER);
    }

    @AfterClass
    public void finishTests(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        browser.manage().deleteAllCookies();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        browser.quit();
//        if (Browser.isRemote){
//            browser.quit();
//        }
    }
}
