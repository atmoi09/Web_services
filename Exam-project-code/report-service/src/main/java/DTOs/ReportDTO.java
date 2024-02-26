package DTOs;

import domain.Payment;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Gunn
 */
@Data
public class ReportDTO implements Serializable {
    private static final long serialVersionUID = 245731306991003459L;
    List<Payment> reportList;
    String errorMessage;
}
