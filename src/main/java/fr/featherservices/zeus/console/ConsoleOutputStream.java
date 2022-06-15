package fr.featherservices.zeus.console;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConsoleOutputStream extends PrintStream {

    private PrintStream logs;
    private Console console;

    public ConsoleOutputStream(Console console, OutputStream out, PrintStream logs) {
        super(out);
        this.logs = logs;
        this.console = console;
    }

    @SuppressWarnings("resource")
    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.printf(l, "[" + date + " Info]" + format, args);
        PrintStream stream = super.printf(l, "[" + date + " Info]" + format, args);
        console.unstashLine();
        return stream;
    }

    @SuppressWarnings("resource")
    @Override
    public PrintStream printf(String format, Object... args) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.printf("[" + date + " Info]" + format, args);
        PrintStream stream = super.printf("[" + date + " Info]" + format, args);
        console.unstashLine();
        return stream;
    }

    @Override
    public void println() {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]");
        super.println("[" + date + " Info]");
        console.unstashLine();
    }

    @Override
    public void println(boolean x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(char x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(char[] x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + String.valueOf(x));
        super.println("[" + date + " Info]" + String.valueOf(x));
        console.unstashLine();
    }

    @Override
    public void println(double x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(float x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(int x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(long x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(Object x) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info]" + x);
        super.println("[" + date + " Info]" + x);
        console.unstashLine();
    }

    @Override
    public void println(String string) {
        console.stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info] " + string);
        super.println("[" + date + " Info] " + string);
        console.unstashLine();
    }
}
