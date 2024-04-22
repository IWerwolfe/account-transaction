package app;

import org.apache.logging.log4j.Level;

public interface Observer {
    void logging(Level level, String message);

}
