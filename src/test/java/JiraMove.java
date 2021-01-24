import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;



public class JiraMove {
    public static WebDriver browser = new ChromeDriver();

    public static void login(String URL) {
        browser.manage().window().maximize();
        browser.get("https://google.com");
    }
}

