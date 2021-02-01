import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JiraItemCreator {

    final public static class JiraItemCreatingError extends Error {
        public JiraItemCreatingError(List<String> errors) {
            super("Cannot create an item because of:\n\t" + String.join("\n\t", errors));
        }
    }

    final public static class JiraItem {
        final public String itemKey;
        final public int itemId;

        public JiraItem(String k, int i) {
            itemKey = k;
            itemId = i;
        }

        @Override
        public String toString() {
            return String.format("(%d) %s ", itemId, itemKey);
        }
    }

    private String jSessionId = null;
    private String projectKey = null;
    private String issueTypeDN = null;
    private String priorityName = "Issue";
    private List<String> labels = new ArrayList<>();
    private String content = null;
    private String summary = null;
    private List<String> errors = new ArrayList<>();
    private String url = "https://jira.ithillel.com/rest/api/2";

    public JiraItemCreator (String projectKey) {
        HttpRequestBuilder.HttpResponse resp = new HttpRequestBuilder()
                .httpMethod(HttpRequestBuilder.HTTPMethod.GET)
                .send(url+"/project");
final JSONObject response = new JSONObject(resp.responseBody.toString());





    }




    public JiraItemCreator setCookie() {
        HttpRequestBuilder.HttpResponse htrb = new HttpRequestBuilder()
                .httpMethod(HttpRequestBuilder.HTTPMethod.HEAD)
                .send(url);
        String cookie = null;
        if (htrb.getResponseHeaders().containsKey("Cookie")) {
            cookie = htrb.getResponseHeaders().get("Cookie").get(0);
            cookie.split(";")[0].replace("JSESSIONID=", "");
            jSessionId = cookie;
        } else {
            errors.add("\'Cookie\' header does not exist in response");
        }
        return this;
    }




//        HashMap<String, List<String>> cookie = new HashMap<>();
//        htrb.getResponseHeaders().containsKey("Cookie") ? cookie.putAll(htrb.getResponseHeaders()) :
}
