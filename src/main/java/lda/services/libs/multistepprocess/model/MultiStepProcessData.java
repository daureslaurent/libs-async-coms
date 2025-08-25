package lda.services.libs.multistepprocess.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Serdeable
public class MultiStepProcessData<T> {
    private int currentStep;
    private String process;

    private boolean finish;
    private boolean retry;
    private Integer delay;
    private T data;
}
