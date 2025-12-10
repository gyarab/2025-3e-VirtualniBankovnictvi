package cz.gyarabProject.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Requests {
    static void main() throws IOException, InterruptedException {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-gateway.kb.cz/sandbox/client-registration/v3/software-statements"));

        request.header("x-correlation-id", "c41ca7c4-5935-4e65-9729-8ce0985225b4");
        String key = "eyJ4NXQiOiJPVFZoTW1Kak1tVXdNV001WVdJMFlUUmlZakk0WldSaU1EWmpNR05tTW1Fd016SXlZVEEyT0E9PSIsImtpZCI6ImFwaW1fYXBpbS1uZGJfa2JjbG91ZF9TU0xDZXJ0aWZpY2F0ZUZpbGUiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJLQi1DVVNcL2FwaUBrYi5jekBjYXJib24uc3VwZXIiLCJhcHBsaWNhdGlvbiI6eyJvd25lciI6IktCLUNVU1wvYXBpQGtiLmN6IiwidGllclF1b3RhVHlwZSI6bnVsbCwidGllciI6IlVubGltaXRlZCIsIm5hbWUiOiJDbGllbnRSZWdpc3RyYXRpb25TYW5kYm94X3YzXzE3Mjc4ODA0MDEwODUiLCJpZCI6NjQ0LCJ1dWlkIjoiNDMwOTc4NTMtNTRhOC00YmE0LTliZGYtYjBiMzVlZTFjZjNiIn0sImlzcyI6Imh0dHBzOlwvXC9hcGltLmFwaW0tbmRiLmtiY2xvdWRcL29hdXRoMlwvdG9rZW4iLCJ0aWVySW5mbyI6eyJDb3BwZXIiOnsidGllclF1b3RhVHlwZSI6InJlcXVlc3RDb3VudCIsImdyYXBoUUxNYXhDb21wbGV4aXR5IjowLCJncmFwaFFMTWF4RGVwdGgiOjAsInN0b3BPblF1b3RhUmVhY2giOnRydWUsInNwaWtlQXJyZXN0TGltaXQiOjAsInNwaWtlQXJyZXN0VW5pdCI6InNlYyJ9fSwia2V5dHlwZSI6IlNBTkRCT1giLCJzdWJzY3JpYmVkQVBJcyI6W3sic3Vic2NyaWJlclRlbmFudERvbWFpbiI6ImNhcmJvbi5zdXBlciIsIm5hbWUiOiJDbGllbnRSZWdpc3RyYXRpb25TYW5kYm94IiwiY29udGV4dCI6Ilwvc2FuZGJveFwvY2xpZW50LXJlZ2lzdHJhdGlvblwvdjMiLCJwdWJsaXNoZXIiOiJvcGVuYmFua2luZ0BrYi5jeiIsInZlcnNpb24iOiJ2MyIsInN1YnNjcmlwdGlvblRpZXIiOiJDb3BwZXIifV0sInRva2VuX3R5cGUiOiJhcGlLZXkiLCJpYXQiOjE3Mjc4ODA0MDEsImp0aSI6Ijc2YzI5MDEzLWYwOGUtNGYyMy1hODZmLTMyMTI4M2I0MTgxMyJ9.wVxbYuljM9DUpxZkGfDF-nJAshjJU0gXzxoL5n-P6lOTm87RMP6RRFp6hXws2jLSO7qfzXTZPAC5gkDXB39oTCMqnD6qTz8Ufkvmp69g4roq7wVXedJoizvXyo-RIoyXEpYR9TAgOvboSSdeKl3TWk6gsNv5inDcb_5KeOevA4SB3rvuH88a8Vun3FTAYWuIF1RcMUOmUrHVZ2bwZ8O7OiGeetroysS4a_aaSBVBiqy9Paij3xRBWH2xw3eDUKP9feoZ-1IrTWXt-6nDOLS_yndovgYMXtFG9jO12eDeY5Z9o0Z-QwhRIY5HgHsX1mCIH2fQckwFH0r7fTm21_uTqQ==";
        request.header("apiKey", key);
        request.header("Content-Type", "application/json");

        String body = """
            {
              "softwareName": "Nejlepší produkt",
              "softwareNameEn": "Best product",
              "softwareId": "f64bf2e447e545228c78e07b081a82ee",
              "softwareVersion": "1.0",
              "softwareUri": "https://client.example.org",
              "redirectUris": [
                "https://client.example.org/callback",
                "https://client.example.org/callback-backup"
              ],
              "tokenEndpointAuthMethod": "client_secret_post",
              "grantTypes": [
                "authorization_code"
              ],
              "responseTypes": [
                "code"
              ],
              "registrationBackUri": "https://client.example.org/backuri",
              "contacts": [
                "email: example@goodsoft.com"
              ],
              "logoUri": "https://client.example.org/logo.png",
              "tosUri": "https://client.example.org/tos",
              "policyUri": "https://client.example.org/policy"
            }""";
        request.POST(HttpRequest.BodyPublishers.ofString(body));
        HttpResponse<String> response = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build()
                .send(request.build(), HttpResponse.BodyHandlers.ofString());

        System.out.println(response.toString());
        System.out.println(response.body());
    }
}
