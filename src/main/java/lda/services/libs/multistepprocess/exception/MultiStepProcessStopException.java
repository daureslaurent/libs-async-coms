package lda.services.libs.multistepprocess.exception;

import lombok.Getter;

@Getter
public class MultiStepProcessStopException extends RuntimeException {
    Object data;
    String desc;

    public MultiStepProcessStopException(Object data, String desc) {
        this.desc = desc;
        this.data = data;
    }
}
