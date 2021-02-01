import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class HttpRequestBuilder {

    public final static class CannotBuildHttpRequest extends Error {
        public CannotBuildHttpRequest(String msg) {
            super(msg);
        }
    }

    public static final class HttpResponse {
        final public int responseCode;
        final public String responseBody;
        final private Map<String, List<String>> responseHeaders;

        public HttpResponse(int rc, String rb, Map<String, List<String>> rh) {
            responseBody = rb;
            responseCode = rc;
            responseHeaders = rh;
        }

        public Map<String, List<String>> getResponseHeaders() {
            return new HashMap<>(responseHeaders);
        }
    }

    public enum HTTPMethod {
        HEAD(false),
        GET(false),
        POST(true),
        PUT(true);
        protected final boolean bodyNeeded;
        HTTPMethod(boolean bn) {
            bodyNeeded = bn;
        }
    }

    private HTTPMethod method = HTTPMethod.GET;
    private String body = null;
    final private Map<String, String> headers = new HashMap<String, String>() {{
        put("Content-type", "application/JSON");
        put("Authorization", "Basic " + Arrays.toString(new Base64().encodeToString(Creds.creds.getBytes())));
    }};
//    Base64().encode(Creds.creds.getBytes())));
    private void setCookie() {
        String url = "https://jira.ithillel.com/rest/api/2";
        ArrayList<String> response = new ArrayList<>();
        try {
            final HttpsURLConnection httpCon = (HttpsURLConnection) new URL(url).openConnection();
            httpCon.setDoOutput(true);
            try {
                httpCon.setRequestMethod(HTTPMethod.HEAD.name());
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
//            headers.forEach(httpCon::setRequestProperty);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpCon.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if (httpCon.getHeaderFields().containsKey("Set-Cookie")){
                List<String> cookie;
                cookie = httpCon.getHeaderFields().get("Set-Cookie");
                cookie.forEach(str -> {
                    if (str.startsWith("JSESSIONID=")) {
                        headers.put("Cookie", str);
                    }
                });
            };
            } catch (Throwable e) {
            throw new CannotBuildHttpRequest(Arrays
                    .stream(e.getStackTrace())
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n")));
        }
    }

    public HttpRequestBuilder httpMethod(HTTPMethod hm){
        method = hm;
        return this;
    }

    public HttpRequestBuilder withBody(String bdtext){
        body = bdtext;
        return this;
    }

    public HttpRequestBuilder setHeaders(String key, String value){
        if (!headers.containsKey(key)) headers.put(key, value);
        return this;
    }

    public HttpResponse send(String url){
        setCookie();
        try {
            final HttpsURLConnection httpCon = (HttpsURLConnection) new URL(url).openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod(method.name());
//            headers.forEach(httpCon::setRequestProperty);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpCon.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if (method.bodyNeeded) {
                final OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
                out.write(body == null ? "" : body);
                out.close();
            }
                ArrayList<String> response = new ArrayList<>();
                BufferedReader rd;
                if (!method.name().equals(HTTPMethod.HEAD.name())) {
                    try {
                        rd = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
                    } catch (Exception e) {
                        rd = new BufferedReader(new InputStreamReader(httpCon.getErrorStream()));
                    }
                    String line;
                    while ((line = rd.readLine()) != null) {
                        response.add(line);
                    }
                }
                return new HttpResponse(httpCon.getResponseCode(), String.join("", response), httpCon.getHeaderFields());
        }
        catch (Throwable e) {
            throw new CannotBuildHttpRequest(Arrays
                    .stream(e.getStackTrace())
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n")));
        }
    }
}

