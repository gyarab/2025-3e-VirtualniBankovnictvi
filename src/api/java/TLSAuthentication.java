import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TLSAuthentication {
    public void request() throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(Constants.Request.SANDBOX));

        for (String header : Constants.Request.HEADERS) {
            request.header(header, Constants.Request.getHeaderValue(header));
        }

        request.POST(HttpRequest.BodyPublishers.ofString(Constants.Request.BODY));
        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
