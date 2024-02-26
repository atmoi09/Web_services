package mappers;

import DTOs.PaymentReportDTO;
import domain.MerchantPayment;
import domain.Payment;

/**
 *
 * @author Gunn
 */
public class Mapper {

    public static void PaymentReportDTOtoPaymentMapper(PaymentReportDTO paymentReportDTO, Payment payment) {
        payment.setPaymentId(paymentReportDTO.getPaymentId());
        payment.setDescription(paymentReportDTO.getDescription());
        payment.setAmount(paymentReportDTO.getAmount());
        payment.setMerchantId(paymentReportDTO.getMerchantId());
        payment.setCustomerId(paymentReportDTO.getCustomerId());
        payment.setTokenId(paymentReportDTO.getTokenId());
    }

    public static void PaymentReportDTOtoMerchantPaymentMapper(PaymentReportDTO paymentReportDTO, MerchantPayment payment) {
        payment.setPaymentId(paymentReportDTO.getPaymentId());
        payment.setDescription(paymentReportDTO.getDescription());
        payment.setAmount(paymentReportDTO.getAmount());
        payment.setMerchantId(paymentReportDTO.getMerchantId());
        payment.setTokenId(paymentReportDTO.getTokenId());
    }
}