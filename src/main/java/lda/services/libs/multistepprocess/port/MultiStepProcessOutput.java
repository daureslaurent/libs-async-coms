package lda.services.libs.multistepprocess.port;

public interface MultiStepProcessOutput<T> {
    void runAsyncProcess(final T data, final String process);
    void injectAsyncProcess(final T data, final String process);
}
