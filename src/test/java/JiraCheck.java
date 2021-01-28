import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Properties {
    public WebDriver browser = Browser.getDriver();
    final public static String URL = "https://jira.ithillel.com/browse/AQ-36";

    @BeforeTest
    public void testLogin(){
        JiraMove.loginURL(URL);
    }

    @Test
    public void verifyLabels(){
        JiraMove.getLabels();
        JiraMove.addLabels(System.getProperty("label"));
        JiraMove.assignItemTo();
        String actualResult = browser.findElement(By.id("assignee-val")).getText();
        Assert.assertEquals(actualResult, "Artem Lapyn", "Results are not equals");
    }

    @AfterTest
    public void cleanTest(){
        JiraMove.assignItemTo();
        browser.quit();
    }
}
