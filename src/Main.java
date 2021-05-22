import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Main {

    /**
     * 日志输出器。
     */
    private static PrintStream log = System.out;

    /**
     * 游戏默认语言：英语。
     */
    private static final String DEFAULT_LANGUAGE = "EN";

    /**
     * 语言属性，表明正在运行的游戏的所使用的语言。<br>
     * 相关语言的资料信息会被存放在对应的 language.txt 文件中。
     */
    private static String language;

    /**
     * 文本对应语言具体内容存储的文件夹。
     */
    private static final File languageFileDir = new File(".\\Source\\Content");

    /**
     * 从传入游戏的参数中读取游戏运行语言。 <br>
     * 自动判断并尝试获取对应的游戏语言信息。
     * @param args 游戏参数。
     */
    private static void initLanguage(String[] args) {
        String reg = "-((la(ng(uage)?)?)|(LANGUAGE))\\s?=\\s?(?<LA>[A-Z]{2})";
        Pattern languageCommandPattern = Pattern.compile(reg);
        List<String> properLanguageCommands = Arrays.stream(args).filter(nowStr -> nowStr.matches(reg))
                .map(command -> {
                    Matcher matcher = languageCommandPattern.matcher(command);
                    if (!matcher.find()) {
                        return null;
                    }
                    return matcher.group("LA");
                })
                .collect(Collectors.toList());
        if (properLanguageCommands.size() > 1) {
            log("Different languages conflicts: " + properLanguageCommands);
        }
        if (properLanguageCommands.size() == 1) {
            language = properLanguageCommands.get(0);
            boolean flag = false;
            if (languageFileDir.exists()) {
                String[] fileNameList = languageFileDir.list();
                if (fileNameList != null) {
                    for (String s : fileNameList) {
                        if (s.length() == 6 && s.substring(0, 2).equals(language)) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
            if (flag) {
                return;
            }
            log("Cannot find the language file " + language + ".txt!");
        }
        language = DEFAULT_LANGUAGE;
    }

    private static Dimension preferredFrameDimension = null;

    /**
     * 标记框架是否全屏显示，True 表示全屏显示。
     */
    private static boolean frameFullScreen ;

    /**
     * 初始化游戏窗口大小：<br>
     * -windowed 使游戏窗口化 <br>
     * -w=800 使游戏窗口的宽度为 800 <br>
     * -h=600 使游戏窗口的宽度为 600
     * @param args 游戏传入参数
     */
    private static void initScreenSize(String[] args) {
        frameFullScreen = ! (Arrays.asList(args).contains("-windowed")
                || Arrays.asList(args).contains("-windowd"));
        if (frameFullScreen) {
            return;
        }
        String widthRegex = "\\A-w(idth)?=(?<WIDTH>\\d+)";
        Pattern widthSearchPattern = Pattern.compile(widthRegex);
        String heightRegex = "\\A-h(eight)?=(?<HEIGHT>\\d+)";
        Pattern heightSearchPattern = Pattern.compile(heightRegex);
        boolean widthSearch = false;
        boolean heightSearch = false;
        Dimension windowScreen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(windowScreen.width, 800);
        int height = Math.min(windowScreen.height, 600);
        int nowValue;
        Matcher widthMatch ;
        Matcher heightMatch ;
        for (String arg : args) {
            widthMatch = widthSearchPattern.matcher(arg);
            if (widthMatch.find()) {
                try {
                    nowValue = Integer.parseInt(widthMatch.group("WIDTH"));
                }
                catch (NumberFormatException n) {
                    log(n.getMessage());
                    continue;
                }
                if (nowValue > windowScreen.width) {
                    log("Width arguments set as " + nowValue + ", but the window's width " +
                            "is only " + windowScreen.width + ".");
                    continue;
                }
                if (widthSearch) {
                    log("Conflicts when more than one parameters for width settings." +
                            " The original value is " + width + ", the surplus value is " + nowValue + ".");
                    continue;
                }
                widthSearch = true;
                width = nowValue;
            }
            heightMatch = heightSearchPattern.matcher(arg);
            if (heightMatch.find()) {
                try {
                    nowValue = Integer.parseInt(heightMatch.group("HEIGHT"));
                }
                catch (NumberFormatException n) {
                    log(n.getMessage());
                    continue;
                }
                if (nowValue > windowScreen.height) {
                    log("Height arguments set as " + nowValue + ", but the window's height " +
                            "is only " + windowScreen.height + ".");
                    continue;
                }
                if (heightSearch) {
                    log("Conflicts when more than one parameters for height settings." +
                            " The original value is " + height + ", the surplus value is " + nowValue + ".");
                    continue;
                }
                heightSearch = true;
                height = nowValue;
            }
        }
        preferredFrameDimension = new Dimension(width, height);
    }

    public static void main(String[] args) {
        initLanguage(args);
        initScreenSize(args);
    }

    /**
     * 静态输出日志的方法。<br>
     * 输出到日志上的内容包括输出的时间 + 打印到日志上的具体内容 content.
     * @param content 打印到日志的具体内容。
     */
    public static void log(String content) {
        log.println(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " " + content);
    }
}
