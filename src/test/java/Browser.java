import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public abstract class Browser {
    private static WebDriver driver;
    private static final DesiredCapabilities caps = new DesiredCapabilities();
    final private static String remoteURL = "http://10.0.0.69:4444/wd/hub";
    final private static List<String> arguments = List
            .of("--headless","--window-size=1920,1080", "--auto-open-devtools-for-tabs");

    public static WebDriver getDriver (){
        final String path = System.getProperty("user.dir");
        if (driver == null) {
            if (System.getProperty("browser").contentEquals("firefox")) {
                System.setProperty("webdriver.gecko.driver", path + "/bin/geckodriver.exe");
                if (System.getenv("REMOTE") != null && System.getenv("REMOTE").toLowerCase().contentEquals("true")) {
                    FirefoxOptions fOptions = new FirefoxOptions().addArguments(arguments);
                    caps.setCapability(ChromeOptions.CAPABILITY,  fOptions);
                    try {
                        driver = new RemoteWebDriver(new URL(remoteURL), DesiredCapabilities.firefox());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else driver = new FirefoxDriver();
            } else {
                System.setProperty("webdriver.chrome.driver", path + "/bin/chromedriver.exe");
                if (System.getenv("REMOTE") != null && System.getenv("REMOTE").toLowerCase().contentEquals("true")){
                    ChromeOptions chOptions = new ChromeOptions().addArguments(arguments);
                    caps.setCapability(ChromeOptions.CAPABILITY,  chOptions);
                    try {
                        driver = new RemoteWebDriver(new URL(remoteURL), DesiredCapabilities.chrome());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else driver = new ChromeDriver();
            }
        }
        return driver;
    }
}
