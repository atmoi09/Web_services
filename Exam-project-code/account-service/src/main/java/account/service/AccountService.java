package account.service;

import account.service.domain.Account;
import account.service.dtos.AccountDTO;
import account.service.dtos.BankAccountRequestDTO;
import account.service.dtos.Mapper;
import messaging.Event;
import messaging.MessageQueue;

public class AccountService {
    public static final String ACCOUNT_REQUESTED = "AccountRequested";
    public static final String ACCOUNT_PROVIDED = "AccountProvided";
    public static final String ACCOUNT_DELETION_REQUESTED = "AccountDeletionRequested";
    public static final String ACCOUNT_CHECK_REQUESTED = "AccountCheckRequested";
    public static final String ACCOUNT_EXISTS = "AccountExists";
    public static final String ACCOUNT_CHECK_RESULT_PROVIDED = "AccountCheckResultProvided";
    public static final String ACCOUNT_NON_EXISTENT = "AccountNonExistent";

    public static final String BANK_ACCOUNT_REQUESTED = "BankAccountRequested";
    public static final String BANK_ACCOUNT_PROVIDED = "BankAccountProvided";
    public static final String ACCOUNT_DELETED = "AccountDeleted";
    AccountRepository accountRepository;
    MessageQueue queue;

    public AccountService(MessageQueue q, AccountRepository accountRepository) {
        this.queue = q;
        this.accountRepository = accountRepository;
        this.queue.addHandler(ACCOUNT_REQUESTED, this::handleAccountRequested);
        this.queue.addHandler(BANK_ACCOUNT_REQUESTED, this::handleGetBankAccountRequested);
        this.queue.addHandler(ACCOUNT_DELETION_REQUESTED, this::handleAccountDeletionRequested);
        this.queue.addHandler(ACCOUNT_CHECK_REQUESTED, this::handleAccountCheckRequested);
    }

    /**
     * @author Gunn
     */
    public void handleAccountRequested(Event ev){
        var account = new Account();
        var accountDTOReceived = ev.getArgument(0, AccountDTO.class);
        var correlationId = ev.getArgument(1, CorrelationId.class);
        Mapper.mapAccountDTOToAccount(accountDTOReceived, account);
        AccountDTO accountDTO = new AccountDTO();
        Mapper.mapAccountToDTO(account, accountDTO);
        try {
            var accountId = accountRepository.createAccount(account);
            accountDTO.setAccountId(accountId);
            Event event = new Event(ACCOUNT_PROVIDED, new Object[] { accountDTO, correlationId });
            queue.publish(event);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            accountDTO.setErrorMessage(e.getMessage());
            Event event = new Event(ACCOUNT_EXISTS, new Object[] {accountDTO, correlationId});
            queue.publish(event);
        }
    }

    /**
     * @author Josephine
     */
    public void handleGetBankAccountRequested(Event ev) {
        BankAccountRequestDTO bankAccountRequestDTO = ev.getArgument(0, BankAccountRequestDTO.class);
        CorrelationId correlationId = ev.getArgument(1, CorrelationId.class);
        Account customerAccount = accountRepository.getAccount(bankAccountRequestDTO.getCustomerId());
        Account merchantAccount = accountRepository.getAccount(bankAccountRequestDTO.getMerchantId());
        if (customerAccount != null) {
            bankAccountRequestDTO.setCustomerBankAccount(customerAccount.getBankAccount());
        }
        else {
            bankAccountRequestDTO.setErrorMessage("Customer not found");
        }
        if (merchantAccount != null) {
            bankAccountRequestDTO.setMerchantBankAccount(merchantAccount.getBankAccount());
        }
        else {
            bankAccountRequestDTO.setErrorMessage("Merchant not found");
        }
        Event event = new Event(BANK_ACCOUNT_PROVIDED, new Object[] {bankAccountRequestDTO, correlationId});
        queue.publish(event);
    }

    /**
     * @author Florian
     */
    public void handleAccountDeletionRequested(Event ev) {
        var accountId = ev.getArgument(0, String.class);
        CorrelationId correlationId = ev.getArgument(1, CorrelationId.class);
        var account = accountRepository.deleteAccount(accountId);
        if (account != null){
            queue.publish(new Event(ACCOUNT_DELETED, new Object[]{accountId, correlationId}));
        }
        else {
            queue.publish(new Event(ACCOUNT_NON_EXISTENT, new Object[]{accountId, correlationId}));
        }
    }

    /**
     * @author Florian
     */
    public void handleAccountCheckRequested(Event ev) {
        var accountId = ev.getArgument(0, String.class);
        CorrelationId correlationId = ev.getArgument(1, CorrelationId.class);
        var truth = accountRepository.checkAccountExists(accountId);
        queue.publish(new Event(ACCOUNT_CHECK_RESULT_PROVIDED, new Object[]{truth, correlationId}));
    }
}



