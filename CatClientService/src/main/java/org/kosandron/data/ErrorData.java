package org.kosandron.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorData {
    private String message;
    private LocalDateTime time;
}
