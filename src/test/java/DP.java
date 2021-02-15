import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class DP {
    @DataProvider(name = "data-provider")
    public Object[][] dpMethod (Method m) {
        switch (m.getName()) {
            case "jiraIssueFullOk": {
                return new Object[][] {{"AQ", "Ошибка", "Medium", "Test jiraIssueFullOk", new ArrayList<String>(Arrays.asList("AQA", "Lapin", "anylabel")), "STR:\n 2. Test2\n 3. Test3"}};
            }
            case "jiraIssueOnError": {
                return new Object[][] {{"AQ", "Story"}};
            }
            default:
                throw new IllegalStateException("Unexpected value: " + m.getName());
        }
    }
}

