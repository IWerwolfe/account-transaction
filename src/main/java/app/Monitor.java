package app;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Monitor implements Observer {

    private final Logger logger = LogManager.getLogger();

    @Override
    public void logging(Level level, String message) {
        logger.log(level, message);
    }
}
