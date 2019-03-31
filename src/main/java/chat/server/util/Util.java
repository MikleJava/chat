package chat.server.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

public final class Util {
    @NotNull
    public static final SimpleDateFormat sdfHMS = new SimpleDateFormat("HH:mm:ss");
    @NotNull
    public static final SimpleDateFormat sdfHM = new SimpleDateFormat("HH:mm");

    public static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
