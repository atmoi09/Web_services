package dtos;

import domain.CorrelationId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Florian
 */
@Data
public class TokenValidationDTO implements Serializable {
    private static final long serialVersionUID = 8009166186081976286L;
    private String customerToken;
    private String customerId;
    private String errorMessage;
}
