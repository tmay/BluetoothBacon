package tmay.bluetoothbacon.ledstrip.util;

import org.androidannotations.annotations.EBean;

/**
 * Created by Terry on 10/4/14.
 */

public class LedstripFrameManager {
    public static byte[] buildMessageFrame(byte[] message) {
        byte[] escaped = new byte[16];
        int escapedCount = 0;
        escaped[escapedCount++] = 0x12;

        for (int i = 0; i < message.length; i++) {
            if (message[i] == 0x12) {
                escaped[escapedCount++] = 0x7d;
            } else if (message[i] == 0x13) {
                escaped[escapedCount++] = 0x7d;
            } else if (message[i] == 0x7d) {
                escaped[escapedCount++] = 0x7d;
            }
            escaped[escapedCount++] = message[i];
        }

        escaped[escapedCount++] = 0x13;
        byte[] trimmed = new byte[escapedCount];
        for (int i = 0; i < escapedCount; i++) {
            trimmed[i] = escaped[i];
        }
        return trimmed;
    }
}
