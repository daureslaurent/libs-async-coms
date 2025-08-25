package lda.services.libs.multistepprocess.exception;

import lombok.Getter;

@Getter
public class MultiStepProcessRetryException extends RuntimeException {
    public static final int DEFAULT_DELAY = 5000;

    Object data;
    int delay;
    String desc;

    public MultiStepProcessRetryException(Object data, int delay, String desc) {
        this.desc = desc;
        this.data = data;
        this.delay = delay;
    }

    public MultiStepProcessRetryException(Object data, String desc) {
        this.desc = desc;
        this.data = data;
        this.delay = DEFAULT_DELAY;
    }
}
