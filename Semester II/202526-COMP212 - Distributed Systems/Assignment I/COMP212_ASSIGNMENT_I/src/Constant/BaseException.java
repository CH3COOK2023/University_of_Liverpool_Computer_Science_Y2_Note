package Constant;

public class BaseException extends RuntimeException {
    /**
     * 抛出运行时异常，终止程序
     * @param message 异常信息，请使用 baseExceptionType 定义
     */
    public BaseException(String message) {
        super(message);
    }
}
