import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
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
            return String.format("(%d): %s ", itemId, itemKey);
        }
    }

    private String projectKey = null;
    private int projectId = -1;
    private int issueTypeId = -1;
    private int priorityId = 5;
    final private List<String> labels = new ArrayList<>();
    private String content = null;
    private String summary = null;
    final private List<String> errors = new ArrayList<>();
    final private String url = "https://jira.ithillel.com/rest/api/2/";

    public JiraItemCreator(String projectKey) {
        HttpRequestBuilder.HttpResponse resp = new HttpRequestBuilder()
                .send(String.format("%sproject", url));
        if (resp.responseCode != 200) {
            errors.add(String
                    .format("Cannot obtain projects with current request. Response code: %d", resp.responseCode));
        } else {
            final JSONArray projects = new JSONArray(resp.responseBody);
            for (int i = 0; i < projects.length(); i++) {
                if (projects.getJSONObject(i).getString("key").contentEquals(projectKey)) {
                    this.projectId = projects.getJSONObject(i).getInt("id");
                    this.projectKey = projectKey;
                    break;
                }
            }
        }
        if (projectId == -1) {
            errors.add(String.format("Project %s does not exist in current Dashboard", projectKey));
            this.projectId = 0;
        }
    }

    private boolean dataIsValid(String data) {
        return data != null & !data.trim().isEmpty();
    }

    private String getReporterName() {
        return Creds.creds.split(":")[0];
    }

    public JiraItemCreator ofType(String issueTypeDisplayName) {
        HttpRequestBuilder.HttpResponse resp = new HttpRequestBuilder()
                .httpMethod(HttpRequestBuilder.HTTPMethod.GET)
                .send(String.format("%sproject/%s",url, projectKey));
        if (resp.responseCode != 200) {
            errors.add(String
                    .format("Cannot obtain issue types with current request. Response code: %d", resp.responseCode));
        }
        final JSONArray issue = new JSONObject(resp.responseBody).getJSONArray("issueTypes");
        for (int i = 0; i < issue.length(); i++) {
            if (issue.getJSONObject(i).getString("name").contentEquals(issueTypeDisplayName)) {
                this.issueTypeId = issue.getJSONObject(i).getInt("id");
                break;
            }
        }
        if (issueTypeId == -1) {
            errors.add(String.format("Issue type does not exist for project %s ", projectKey));
            this.issueTypeId = 0;
        }
        return this;
    }

    public JiraItemCreator withPriority(String priorityDisplayName) {
        HttpRequestBuilder.HttpResponse resp = new HttpRequestBuilder()
                .send(url + "priority");
        final JSONArray priority = new JSONArray(resp.responseBody);
        for (int i = 0; i < priority.length(); i++) {
            if (priority.getJSONObject(i).getString("name").contentEquals(priorityDisplayName)) {
                this.priorityId = priority.getJSONObject(i).getInt("id");
                break;
            }
        }
        return this;
    }

    public JiraItemCreator withLabels(Collection<String> labels) {
        if (labels.size() == 0) return this;
        labels.forEach(label -> {
            if (dataIsValid(label)) this.labels.add(label);
        });
        return this;
    }

    public JiraItemCreator withDescription(String content) {
        if (dataIsValid(content)) {
            this.content = content;
        } else {
            errors.add("Description is empty or null");
        }
        return this;
    }

    public JiraItemCreator withSummary(String sum) {
        if (dataIsValid(sum)) {
            this.summary = sum;
        }
        return this;
    }

    public JiraItem create() {
        JiraItem created = null;
        final boolean startCreating = projectId > 0 && summary != null && issueTypeId != -1;
        if (projectId == -1) {
            errors.add("Please, define project, System should know ProjectName for creating Jira Item.");
        }
        if (summary == null) {
            errors.add("Summary is empty or not defined. Summary is required. Use withSummary().");
        }
        if (issueTypeId == -1) {
            errors.add("Issue type is required. Please, set issue type with ofType() method.");
        }
        if (startCreating) {
            JSONArray labels = new JSONArray();
            this.labels.stream().distinct().forEach(labels::put);
            JSONObject body = new JSONObject();
            JSONObject fields = new JSONObject();
            fields.put("project", new JSONObject().put("id", projectId));
            fields.put("summary", summary);
            fields.put("description", content);
            fields.put("reporter", new JSONObject().put("name", getReporterName()));
            fields.put("issuetype", new JSONObject().put("id", issueTypeId));
            fields.put("priority", new JSONObject().put("id", "" + priorityId));
            fields.put("labels", labels);
            body.put("fields", fields);
            final HttpRequestBuilder.HttpResponse response = new HttpRequestBuilder()
                    .httpMethod(HttpRequestBuilder.HTTPMethod.POST)
                    .withBody(body.toString())
                    .send(url + "issue");
            if (response.responseCode == 201) {
                JSONObject responseParsed = new JSONObject(response.responseBody);
                created = new JiraItem(responseParsed.getString("key"), responseParsed.getInt("id"));
            } else {
                errors.add(String.format("Cannot create JiraItem. HTTP response code: %s", response.responseCode));
            }
        }
        if (errors.size() > 0) {
            throw new JiraItemCreatingError(errors);
        }
        return created;
    }

    public static void main(String[] args) {
        ArrayList<String> labs = new ArrayList<>();
        labs.add("AQA");
        labs.add("Lapin");
        labs.add("builder");
        JiraItem ji = new JiraItemCreator("AQ")
                .ofType("Bug")
                .withPriority("Medium")
                .withSummary("JiraItem #140")
                .withLabels(labs)
                .withDescription("STR:\n 1. Compose HTTP-builder\n 2. Compose JiraItem-builder\n 3. Send REST Api with Java")
                .create();
        System.out.println(ji.toString());
    }
}

