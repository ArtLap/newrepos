import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;


public class JiraMove {
    private static final WebDriver browser = Browser.getDriver();
    public static WebDriverWait wait = new WebDriverWait(browser, 3);

    public static void loginURL(String URL) {
        browser.manage().window().maximize();
        browser.get(URL);
        if (browser.getTitle().contentEquals("Log in - Jira - Hillel IT School")) {
            final List<WebElement> authFields = browser.findElements(By.cssSelector("input.text.medium-field"));
            String name = System.getProperty("login");
            String password = System.getProperty("password");
            authFields.get(0).sendKeys(name);
            authFields.get(1).sendKeys(password);
            browser.findElement(By.id("login-form-submit")).click();
        }
    }

    public static void getLabels() {
        List<WebElement> labels = browser.findElement(By.className("labels")).findElements(By.cssSelector("span"));
        for (WebElement label : labels) {
            System.out.println(label.getText());
        }
    }

    public static void addLabels(String label) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.labels+span.overlay-icon.aui-icon.aui-icon-small.aui-iconfont-edit")));
        WebElement addLabel = browser
                .findElement(By
                        .cssSelector("ul.labels+span.overlay-icon.aui-icon.aui-icon-small.aui-iconfont-edit"));
        Actions act = new Actions(browser);
        act
                .click(addLabel)
                .sendKeys(label)
                .sendKeys(Keys.SPACE)
                .build()
                .perform();
        WebElement element = browser.findElement(By.cssSelector("button[type=submit]"));
        element.click();
    }

    public static void assignItemTo() {
        WebElement elementSet = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("assignee-val")));
        if (elementSet.getText().contentEquals("Unassigned")) {
            WebElement toMe = browser.findElement(By.id("assign-to-me"));
            toMe.click();
            wait.until(ExpectedConditions.textToBe(By.id("assignee-val"), "Artem Lapyn"));
        } else {
            elementSet.click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("assignee-field")));
            elementSet.findElement(By.id("assignee-field")).sendKeys("Automatic");
            elementSet.findElement(By.cssSelector("button[type=submit]")).click();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void getPriorities() {
        browser.findElement(By.id("priority-val")).click();
        final WebElement priority = wait.until(ExpectedConditions
                .presenceOfElementLocated(By.id("priority")));
        Select select = new Select(priority);
        List<WebElement> options = select.getOptions();
        for (WebElement option : options) System.out.println(option.getAttribute("textContent").trim());
        browser.findElement(By.
                cssSelector("div.ajs-layer-placeholder+span")).click();
        browser.findElement(By.cssSelector("button[type=cancel]")).click();
    }

    public static WebElement getDescriptionElement(){
        wait.until(ExpectedConditions
                .presenceOfElementLocated(By.className("user-content-block"))).click();
        WebElement iframe = wait.until(ExpectedConditions
                .presenceOfElementLocated(By.tagName("iframe")));
        browser.switchTo().frame(iframe);
        WebElement textarea = browser.findElement(By.id("tinymce"));
        return textarea;
    }

    public static String getDescription(){
        WebElement textarea = getDescriptionElement();
        String result = textarea.getText();
        browser.switchTo().parentFrame();
        browser.findElement(By.cssSelector("button[type=submit]")).click();
        return result;
    }

    public static void setDescription(String description) {
        WebElement textarea = getDescriptionElement();
        textarea.clear();
        textarea.sendKeys(description);
        browser.switchTo().parentFrame();
        browser.findElement(By.cssSelector("button[type=submit]")).click();
    }

    public static void printTickets() {
        String currTab = browser.getWindowHandle();
        Actions act = new Actions(browser);
        act
                .keyDown(Keys.CONTROL)
                .click(browser.findElements(By.className("aui-nav-item")).get(4))
                .keyUp(Keys.CONTROL)
                .perform();
        browser.switchTo().window(browser.getWindowHandles()
                .stream()
                .filter(tab -> !tab.contains(currTab))
                .collect(Collectors.joining()));
        List<WebElement> issueList = browser.findElements(By.cssSelector("ol.issue-list>li"));
        for(WebElement issue:issueList) {
            System.out.printf(String
                    .format("%s : %s\n", issue.getAttribute("data-key"), issue.getAttribute("title")));
        }
        browser.close();
        browser.switchTo().window(currTab);
    }

    public static void ClearBrowserCache() {
        browser.manage().deleteAllCookies();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}