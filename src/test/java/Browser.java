import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Browser {
    private static WebDriver driver;

    public static WebDriver getDriver (){
        final String path = System.getProperty("user.dir");
        if (driver == null) {
            if (System.getProperty("browser").contentEquals("firefox")) {
                System.setProperty("webdriver.gecko.driver", path + "/bin/geckodriver.exe");
                driver = new FirefoxDriver();
            } else {
                System.setProperty("webdriver.chrome.driver", path + "/bin/chromedriver.exe");
                if (System.getProperty("mode").contentEquals("headless")){
                    ChromeOptions chOptions = new ChromeOptions();
                    chOptions.addArguments("--headless");
                    chOptions.addArguments("--window-size=1920,1080");
                    chOptions.addArguments("auto-open-devtools-for-tabs");
                    driver = new ChromeDriver(chOptions);
                } else driver = new ChromeDriver();

            }
        }
        return driver;
    }
}
