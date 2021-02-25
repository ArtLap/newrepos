import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Collection;

public class JiraCheck {
    public WebDriver browser = Browser.getDriver();
    final public static String URL = "https://jira.ithillel.com/browse/";


    @Test(dataProvider="data-provider", dataProviderClass = DP.class)
    public void jiraIssueFullOk(String pKey, String ofType, String priority, String summary, Collection<String> labels, String desc){
        JiraItemCreator.JiraItem jiraIssueFullOk = new JiraItemCreator(pKey)
                .ofType(ofType)
                .withPriority(priority)
                .withSummary(summary)
                .withLabels(labels)
                .withDescription(desc)
                .create();
        JiraMove.loginURL(URL+jiraIssueFullOk.itemKey);
        JiraMove.getLabels();
//        JiraMove.addLabels(System.getProperty("label"));
        JiraMove.assignItemTo();
        String actualResult = browser.findElement(By.id("assignee-val")).getText();
        Assert.assertEquals(actualResult, "Artem Lapyn", "Results are not equals");
        JiraMove.getPriorities();
        String description = "Lorem Ipsum Dolor Sit Amet";
        JiraMove.setDescription(description);
        Assert.assertEquals(description, JiraMove.getDescription(), "Values not matched");
        JiraMove.printTickets();
    }

    @Test(dataProvider ="data-provider", dataProviderClass = DP.class)
    protected void jiraIssueOnError(String pKey, String ofType){
        Assert.expectThrows(JiraItemCreator.JiraItemCreatingError.class, ()->{
            new JiraItemCreator(pKey)
                    .ofType(ofType)
                    .create();
        });
    }

    @AfterClass
    public void finish(){
        JiraMove.ClearBrowserCache();
        JiraMove.assignItemTo();
        browser.quit();
    }
}
