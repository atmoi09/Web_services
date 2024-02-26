package report.service;

import domain.MerchantPayment;
import domain.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportRepository {
    private Map<String, List<Payment>> customerReport;
    private Map<String, List<MerchantPayment>> merchantReport;
    private List<Payment> managerReport;

    public ReportRepository() {
        this.customerReport = new HashMap<>();
        this.merchantReport = new HashMap<>();
        this.managerReport = new ArrayList<>();
    }


    /**
     *
     * @author Gunn
     */
    public void addPaymentToCustomerList(String id, Payment payment) {
        if (customerReport.containsKey(id)) {
            customerReport.get(id).add(payment);
        } else {
            List<Payment> payments = new ArrayList<>();
            payments.add(payment);
            customerReport.put(id, payments);
        }
    }

    /**
     *
     * @author Josephine
     */
    public void addPaymentToMerchantList(String id, MerchantPayment payment) {
        System.out.println(id);
        if (merchantReport.containsKey(id)) {
            merchantReport.get(id).add(payment);
        } else {
            List<MerchantPayment> payments = new ArrayList<>();
            payments.add(payment);
            merchantReport.put(id, payments);
        }
    }

    /**
     *
     * @author Josephine
     */
    public void addPaymentToManagerList(Payment payment) {
        managerReport.add(payment);
    }

    /**
     *
     * @author Gunn
     */
    public List<Payment> getCustomerReportById(String id) {
        if (customerReport.containsKey(id)) {
            return customerReport.get(id);
        }
        return new ArrayList<>();
    }

    /**
     *
     * @author Josephine
     */
    public List<MerchantPayment> getMerchantReportById(String id) {
        if (merchantReport.containsKey(id)) {
            return merchantReport.get(id);
        }
        return new ArrayList<>();
    }

    /**
     *
     * @author Gunn
     */
    public List<Payment> getManagerReport() {
        return managerReport;
    }
}

