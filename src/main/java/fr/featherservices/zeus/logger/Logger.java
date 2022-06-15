package fr.featherservices.zeus.logger;

import java.util.Calendar;
import java.util.Formatter;

public class Logger {
    private Formatter format;
    private Calendar gfg_calender;

    public Logger() {
        format = new Formatter();
        // Creating a calendar
        gfg_calender = Calendar.getInstance();
    }

    public void log(String log, LogType logType) {
        format.format("%tl:%tM", gfg_calender, gfg_calender);
        if (logType.equals(LogType.INFO))
            System.out.println("[" + format + "] " + log);
        else
            System.out.println("[" + format + " - " + logType.getDisplay() + "] " + log);
    }

}
