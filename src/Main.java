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

    public static void main(String[] args) {
        initLanguage(args);

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
