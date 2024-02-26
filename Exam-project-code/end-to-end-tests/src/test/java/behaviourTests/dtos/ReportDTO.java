package behaviourTests.dtos;

import behaviourTests.domain.Payment;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Bingkun
 */
@Data
public class ReportDTO implements Serializable {
    private static final long serialVersionUID = 245731306991003459L;
    List<Payment> reportList;
    String errorMessage;
}
