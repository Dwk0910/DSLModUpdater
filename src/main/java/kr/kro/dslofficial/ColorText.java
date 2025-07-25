package kr.kro.dslofficial;

// JANSI Import
import org.fusesource.jansi.*;

public class ColorText {
    public static String text(String text, String color, String background, boolean isBold, boolean isItalic, boolean isUnderlined) {
        AnsiConsole.systemInstall();

        Ansi result = new Ansi();
        switch (color) {
            case "white" -> result = Ansi.ansi().fgRgb(255, 255, 255);
            case "black" -> result = Ansi.ansi().fgRgb(0, 0, 0);
            case "gray" -> result = Ansi.ansi().fg(158200);
            case "red" -> result = Ansi.ansi().fgRed();
            case "green" -> result = Ansi.ansi().fgGreen();
            case "yellow" -> result = Ansi.ansi().fgYellow();
            case "blue" -> result = Ansi.ansi().fgBlue();
            case "magenta" -> result = Ansi.ansi().fgMagenta();
            case "cyan" -> result = Ansi.ansi().fgCyan();
            case "b-black" -> result = Ansi.ansi().fgBrightBlack();
            case "b-red" -> result = Ansi.ansi().fgBrightRed();
            case "b-green" -> result = Ansi.ansi().fgBrightGreen();
            case "b-yellow" -> result = Ansi.ansi().fgBrightYellow();
            case "b-blue" -> result = Ansi.ansi().fgBrightBlue();
            case "b-magenta" -> result = Ansi.ansi().fgBrightMagenta();
            case "b-cyan" -> result = Ansi.ansi().fgBrightCyan();
        }

        if (!background.isEmpty()) {
            switch (background) {
                case "white" -> result = result.bgRgb(255, 255, 255);
                case "red" -> result = result.bgRed();
                case "green" -> result = result.bgGreen();
                case "yellow" -> result = result.bgYellow();
                case "magenta" -> result = result.bgMagenta();
                case "cyan" -> result = result.bgCyan();
                case "b-red" -> result = result.bgBrightRed();
                case "b-green" -> result = result.bgBrightGreen();
                case "b-yellow" -> result = result.bgBrightYellow();
                case "b-magenta" -> result = result.bgBrightMagenta();
                case "b-cyan" -> result = result.bgBrightCyan();
            }
        }

        if (isBold) result = result.bold();
        if (isItalic) result.a(Ansi.Attribute.ITALIC);
        if (isUnderlined) result.a(Ansi.Attribute.UNDERLINE);

        result = result.a(text).reset();
        return result.toString();
    }
}