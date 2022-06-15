package fr.featherservices.zeus.console;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConsoleErrorStream extends PrintStream {

    protected final static String ERROR_RED = "\u001B[31;1m";
    protected final static String RESET_COLOR = "\u001B[0m";
    private PrintStream logs;
    private Console console;

    public ConsoleErrorStream(Console console, OutputStream out, PrintStream logs) {
        super(out);
        this.logs = logs;
        this.console = console;
    }

    @SuppressWarnings("resource")
    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.printf(l, "[" + date + " Error]" + format, args);
        PrintStream stream = super.printf(l, ERROR_RED + "[" + date + " Error]" + format + RESET_COLOR, args);
        console.unstashLine();
        return stream;
    }

    @SuppressWarnings("resource")
    @Override
    public PrintStream printf(String format, Object... args) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.printf("[" + date + " Error]" + format, args);
        PrintStream stream = super.printf(ERROR_RED + "[" + date + " Error]" + format + RESET_COLOR, args);
        console.unstashLine();
        return stream;
    }

    @Override
    public void println() {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]");
        super.println(ERROR_RED + "[" + date + " Error]" + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(boolean x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(char x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(char[] x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + String.valueOf(x));
        super.println(ERROR_RED + "[" + date + " Error]" + String.valueOf(x) + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(double x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(float x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(int x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(long x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(Object x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error]" + x);
        super.println(ERROR_RED + "[" + date + " Error]" + x + RESET_COLOR);
        console.unstashLine();
    }

    @Override
    public void println(String string) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Error] " + string);
        super.println(ERROR_RED + "[" + date + " Error] " + string + RESET_COLOR);
        console.unstashLine();
    }
}
