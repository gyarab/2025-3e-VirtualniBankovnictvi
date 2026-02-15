package cz.gyarabProject.api.kb.datatype;

public record Code (String code, long createdAt) {
    private static final int EXPIRE_TIME = 180;

    public Code {
        createdAt = System.currentTimeMillis() / 1000;
    }

    public boolean isValid() {
        return createdAt + EXPIRE_TIME > System.currentTimeMillis() / 1000;
    }

    public long getValidity() {
        return createdAt + EXPIRE_TIME;
    }

    @Override
    public String toString() {
        return "code: " + code + ", validity until: " + getValidity();
    }
}
