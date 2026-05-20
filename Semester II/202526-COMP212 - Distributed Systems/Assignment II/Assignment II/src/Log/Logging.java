package Log;

import Configuration.GlobalConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logging {
    // 匹配作业加分项要求：存放在 logs 文件夹，文件名为 voting.log
    private static final String DEFAULT_LOG_PATH = GlobalConfiguration.LOG_FILE_PATH;
    // 日期格式化器
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 全局静态锁，确保多线程下写入同一文件不发生冲突
    private static final Object FILE_LOCK = new Object();

    /**
     * 静态代码块：类加载时自动执行一次，确保日志目录存在
     */
    static {
        initLogFile();
    }

    /**
     * 私有化构造函数，防止外部实例化这个工具类
     */
    private Logging() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 初始化文件目录，确保 logs 文件夹存在
     */
    private static void initLogFile() {
        File file = new File(DEFAULT_LOG_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }

    /**
     * info 级别日志，支持 "{}" 占位符
     */
    public static void info(String message, Object... args) {
        writeLog("info", formatMessage(message, args));
    }

    /**
     * error 级别日志，支持 "{}" 占位符
     */
    public static void error(String message, Object... args) {
        writeLog("error", formatMessage(message, args));
    }

    /**
     * 核心写入逻辑
     */
    private static void writeLog(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);

        // 组装日志格式
        String logContent = String.format("[%s] %s\t| %s%n", timestamp, level, message);

        // 1. 打印到控制台
        if ("error".equals(level)) {
            System.err.print(logContent);
        } else {
            System.out.print(logContent);
        }

        // 2. 追加写入到文件 (加锁保证线程安全)
        synchronized (FILE_LOCK) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DEFAULT_LOG_PATH, true))) {
                writer.write(logContent);
            } catch (IOException e) {
                System.err.println("无法写入日志文件: " + e.getMessage());
            }
        }
    }

    /**
     * 模仿 SLF4J 的字符串替换逻辑：将 "{}" 替换为参数
     */
    private static String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        for (Object arg : args) {
            int index = sb.indexOf("{}");
            if (index != -1) {
                sb.replace(index, index + 2, arg == null ? "null" : arg.toString());
            }
        }
        return sb.toString();
    }
}