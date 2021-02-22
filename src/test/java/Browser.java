import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class Browser {
    public final static class ErrorDefiningBrowser extends Error {
        public ErrorDefiningBrowser(String msg) {
            super(msg);
        }
    }


    private static WebDriver driver;
    private static final DesiredCapabilities caps = new DesiredCapabilities();
    final static boolean isRemote = System.getProperty("REMOTE") != null;
    final private static String remoteURL = "http://10.0.0.69:4444/wd/hub";
    final private static List<String> arguments = Arrays
            .asList("--headless", "--window-size=1920,1080", "--auto-open-devtools-for-tabs");

    public static WebDriver getDriver (){
        final String path = System.getProperty("user.dir");
        final String getBrowser = System.getProperty("browser");
        if (driver == null) {
            if (getBrowser.contentEquals("firefox")) {
                System.setProperty("webdriver.gecko.driver", path + "/bin/geckodriver.exe");
                if (isRemote) {
                    FirefoxOptions fOptions = new FirefoxOptions().addArguments(arguments);
                    caps.setCapability(ChromeOptions.CAPABILITY,  fOptions);
                    try {
                        driver = new RemoteWebDriver(new URL(remoteURL), DesiredCapabilities.firefox());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else driver = new FirefoxDriver();
            } else if (getBrowser.contentEquals("chrome")){
                System.setProperty("webdriver.chrome.driver", path + "/bin/chromedriver.exe");
                if (isRemote){
                    ChromeOptions chOptions = new ChromeOptions().addArguments(arguments);
                    caps.setCapability(ChromeOptions.CAPABILITY,  chOptions);
                    try {
                        driver = new RemoteWebDriver(new URL(remoteURL), DesiredCapabilities.chrome());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                } else driver = new ChromeDriver();
            } else {
                throw new ErrorDefiningBrowser("Please set up valid browser property");
            }
        }
        return driver;
    }
}
