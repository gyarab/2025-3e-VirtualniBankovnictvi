public class Constants {
    public static class Request {
        public static final String SANDBOX = "https://api-gateway.kb.cz/sandbox/client-registration/v3/software-statements";
        public static final String PRODUCTION = "https://client-registration.api-gateway.kb.cz/v3/software-statements";
        public static final String[] HEADERS = {"x-correlation-id", "apiKey", "Content-Type"};
        public static final String BODY = """
                {
                  "softwareName": "example app",
                  "softwareNameEn": "example app",
                  "softwareId": "f64bf2e447e545228c78e07b091a82ee",
                  "softwareVersion": "1.0",
                  "softwareUri": "https://client.example.org",
                
                  "redirectUris": [
                    "https://client.example.org/callback",
                    "https://client.example.org/callback-backup"
                  ],
                
                  "tokenEndpointAuthMethod": "client_secret_post",
                
                  "grantTypes": [
                    "authorization_code",
                    "refresh_token"
                  ],
                
                  "responseTypes": [
                    "code"
                  ],
                
                  "registrationBackUri": "https://client.example.org/backuri",
                
                  "contacts": [
                    "email: hello@example.org"
                  ],
                
                  "logoUri": "https://client.example.org/icon.png",
                  "tosUri": "https://client.example.org/tos",
                  "policyUri": "https://client.example.org/policy"
                }""";

        public static String getHeaderValue(String header) {
            return "";
        }
    }
}

// HttpRequest, HttpClient, KeyStore (or some other class for keys), JSON representation
