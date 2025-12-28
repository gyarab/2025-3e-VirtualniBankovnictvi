package cz.gyarabProject.api.datatype;

import org.springframework.stereotype.Component;

import java.io.Console;

@Component
public class KeyHolder {
    private final String apiKey;

    public KeyHolder() {
        Console console = System.console();
        if (console == null) {
            System.out.println("No console available");
            apiKey = "eyJ4NXQiOiJPVFZoTW1Kak1tVXdNV001WVdJMFlUUmlZakk0WldSaU1EWmpNR05tTW1Fd016SXlZVEEyT0E9PSIsImtpZCI6ImFwaW1fYXBpbS1uZGJfa2JjbG91ZF9TU0xDZXJ0aWZpY2F0ZUZpbGUiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJLQi1DVVNcL2FwaUBrYi5jekBjYXJib24uc3VwZXIiLCJhcHBsaWNhdGlvbiI6eyJvd25lciI6IktCLUNVU1wvYXBpQGtiLmN6IiwidGllclF1b3RhVHlwZSI6bnVsbCwidGllciI6IlVubGltaXRlZCIsIm5hbWUiOiJDbGllbnRSZWdpc3RyYXRpb25TYW5kYm94X3YzXzE3Mjc4ODA0MDEwODUiLCJpZCI6NjQ0LCJ1dWlkIjoiNDMwOTc4NTMtNTRhOC00YmE0LTliZGYtYjBiMzVlZTFjZjNiIn0sImlzcyI6Imh0dHBzOlwvXC9hcGltLmFwaW0tbmRiLmtiY2xvdWRcL29hdXRoMlwvdG9rZW4iLCJ0aWVySW5mbyI6eyJDb3BwZXIiOnsidGllclF1b3RhVHlwZSI6InJlcXVlc3RDb3VudCIsImdyYXBoUUxNYXhDb21wbGV4aXR5IjowLCJncmFwaFFMTWF4RGVwdGgiOjAsInN0b3BPblF1b3RhUmVhY2giOnRydWUsInNwaWtlQXJyZXN0TGltaXQiOjAsInNwaWtlQXJyZXN0VW5pdCI6InNlYyJ9fSwia2V5dHlwZSI6IlNBTkRCT1giLCJzdWJzY3JpYmVkQVBJcyI6W3sic3Vic2NyaWJlclRlbmFudERvbWFpbiI6ImNhcmJvbi5zdXBlciIsIm5hbWUiOiJDbGllbnRSZWdpc3RyYXRpb25TYW5kYm94IiwiY29udGV4dCI6Ilwvc2FuZGJveFwvY2xpZW50LXJlZ2lzdHJhdGlvblwvdjMiLCJwdWJsaXNoZXIiOiJvcGVuYmFua2luZ0BrYi5jeiIsInZlcnNpb24iOiJ2MyIsInN1YnNjcmlwdGlvblRpZXIiOiJDb3BwZXIifV0sInRva2VuX3R5cGUiOiJhcGlLZXkiLCJpYXQiOjE3Mjc4ODA0MDEsImp0aSI6Ijc2YzI5MDEzLWYwOGUtNGYyMy1hODZmLTMyMTI4M2I0MTgxMyJ9.wVxbYuljM9DUpxZkGfDF-nJAshjJU0gXzxoL5n-P6lOTm87RMP6RRFp6hXws2jLSO7qfzXTZPAC5gkDXB39oTCMqnD6qTz8Ufkvmp69g4roq7wVXedJoizvXyo-RIoyXEpYR9TAgOvboSSdeKl3TWk6gsNv5inDcb_5KeOevA4SB3rvuH88a8Vun3FTAYWuIF1RcMUOmUrHVZ2bwZ8O7OiGeetroysS4a_aaSBVBiqy9Paij3xRBWH2xw3eDUKP9feoZ-1IrTWXt-6nDOLS_yndovgYMXtFG9jO12eDeY5Z9o0Z-QwhRIY5HgHsX1mCIH2fQckwFH0r7fTm21_uTqQ=="; //only until it would work
            return;
//            throw new RuntimeException("No console available.");
        }

        apiKey = new String(console.readPassword("write a key for api of this project: "));
    }

    public String getApi() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "";
    }
}
