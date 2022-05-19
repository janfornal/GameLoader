package GameLoader.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * This class is not thread-safe
 */
public class PasswordManager {
    private final Base64.Encoder encoder = Base64.getEncoder();
    private final MessageDigest sha256;
    {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        sha256 = digest;
    }

    public String hash(String username, String password) {
        String inp = "gameloader" + username + password;
        byte[] hsh = sha256.digest(inp.getBytes());
        return new String(hsh);
    }
}
