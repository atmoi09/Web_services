package mappers;

import dtos.BankAccountRequestDTO;
import dtos.PaymentDTO;
import dtos.PaymentReportDTO;
import dtos.TokenValidationDTO;
import domain.Payment;

/**
 * @author Florian
 */
public class Mapper {
    public static void mapPaymentToDTO(Payment payment, PaymentDTO paymentDTO){
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentId(payment.getPaymentId());
        paymentDTO.setCustomerToken(payment.getCustomerToken());
        paymentDTO.setMerchantId(payment.getMerchantId());
        paymentDTO.setErrorDescription(payment.getDescription());
    }

    public static void mapPaymentToPaymentForReportDTO(Payment payment, PaymentReportDTO paymentDTO){
        paymentDTO.setAmount(payment.getAmount());
        paymentDTO.setPaymentId(payment.getPaymentId());
        paymentDTO.setTokenId(payment.getCustomerToken());
        paymentDTO.setMerchantId(payment.getMerchantId());
        paymentDTO.setDescription(payment.getDescription());
    }

    public static void mapPaymentDTOToPayment(PaymentDTO paymentDTO, Payment payment){
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentId(paymentDTO.getPaymentId());
        payment.setCustomerToken(paymentDTO.getCustomerToken());
        payment.setMerchantId(paymentDTO.getMerchantId());
        payment.setDescription(paymentDTO.getErrorDescription());
    }

    public static void mapBankAccountRequestDTO(Payment payment, TokenValidationDTO tokenValidationDTO, BankAccountRequestDTO bankAccountRequestDTO){
        bankAccountRequestDTO.setCustomerId(tokenValidationDTO.getCustomerId());
        bankAccountRequestDTO.setMerchantId(payment.getMerchantId());
    }

    public static void mapTokenValidationDTO(Payment payment, TokenValidationDTO tokenValidationDTO){
        tokenValidationDTO.setCustomerToken(payment.getCustomerToken());
    }
}
