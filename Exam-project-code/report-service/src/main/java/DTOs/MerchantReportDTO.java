package DTOs;

import domain.MerchantPayment;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Josephine
 */
@Data
public class MerchantReportDTO implements Serializable {
    private static final long serialVersionUID = -375527631995190916L;
    List<MerchantPayment> merchantReportList;
    String errorMessage;

}
