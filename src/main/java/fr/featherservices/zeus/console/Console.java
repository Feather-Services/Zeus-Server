package fr.featherservices.zeus.console;

import fr.featherservices.zeus.ZeusServer;
import fr.featherservices.zeus.utils.CustomStringUtils;
import jline.console.ConsoleReader;
import org.jline.reader.*;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Console {

    private final static String CONSOLE = "CONSOLE";
    private final static String PROMPT = "> ";
    protected final static String ERROR_RED = "\u001B[31;1m";
    protected final static String RESET_COLOR = "\u001B[0m";

    private Terminal terminal;
    private LineReader tabReader;
    private ConsoleReader reader;

    private InputStream in;
    @SuppressWarnings("unused")
    private PrintStream out;
    @SuppressWarnings("unused")
    private PrintStream err;
    public PrintStream logs;

    public Console(InputStream in, PrintStream out, PrintStream err) throws IOException {
        String fileName = new SimpleDateFormat("yyyy'-'MM'-'dd'_'HH'-'mm'-'ss'_'zzz'.log'").format(new Date());
        File dir = new File("logs");
        dir.mkdirs();
        File logs = new File(dir, fileName);
        this.logs = new PrintStream(logs);

        if (in != null) {
            System.setIn(in);
            this.in = System.in;
        } else {
            this.in = null;
        }
        System.setOut(new ConsoleOutputStream(this, out == null ? new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                //DO NOTHING
            }
        }) : out, this.logs));
        this.out = System.out;

        System.setErr(new ConsoleErrorStream(this, err == null ? new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                //DO NOTHING
            }
        }) : err, this.logs));
        this.err = System.err;

        reader = new ConsoleReader(in, out);
        reader.setExpandEvents(false);
        reader.setHandleUserInterrupt(false);

        terminal = TerminalBuilder.builder().streams(in, out).system(true).jansi(true).build();
        tabReader = LineReaderBuilder.builder().terminal(terminal).build();
        tabReader.setAutosuggestion(LineReader.SuggestionType.NONE);
    }

    public void run() {
        if (in == null) {
            return;
        }
        while (true) {
            try {
                String command = tabReader.readLine(PROMPT).trim();
                if (command.length() > 0) {
                    String[] input = CustomStringUtils.splitStringToArgs(command);
                    new Thread(() -> ZeusServer.getInstance().dispatchCommand(input)).start();
                }
            } catch (UserInterruptException e) {
                System.exit(0);
            } catch (EndOfFileException e) {
                break;
            }
        }
    }

    protected void stashLine() {
        try {
            tabReader.callWidget(LineReader.CLEAR);
        } catch (Exception ignore) {
        }
    }

    protected void unstashLine() {
        try {
            tabReader.callWidget(LineReader.REDRAW_LINE);
            tabReader.callWidget(LineReader.REDISPLAY);
            tabReader.getTerminal().writer().flush();
        } catch (Exception ignore) {
        }
    }

    public void sendMessage(String message) {
        stashLine();
        String date = new SimpleDateFormat("HH':'mm':'ss").format(new Date());
        logs.println("[" + date + " Info] " + message);
        try {
            reader.getOutput().append("[" + date + " Info] " + message + "\n");
            reader.getOutput().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        unstashLine();
    }

}
