package kr.kro.dslofficial;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class Test {
    public static String str = null;
    public static void main(String[] args) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            String input = reader.readLine("Prompt > ");
            System.out.println(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
