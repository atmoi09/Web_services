package account.service;

import account.service.domain.Account;

import java.util.HashMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class AccountRepository {
    private HashMap<String, Account> accounts;

    //Should be static but for the sake of service testing it isn't
    private int id = 0;

    public AccountRepository() {
        accounts = new HashMap<String,Account>();
    }

    /**
     * @author Bingkun
     */
    private String nextId() {
        id++;
        return Integer.toString(id);
    }

    /**
     * @author Gunn
     */
    public String createAccount(Account account) throws Exception {
        var existingAccount = accounts.values().stream().filter(acc -> acc.getCpr().equals(account.getCpr())).findAny();
        if (!existingAccount.isEmpty()) {
            throw new Exception("Account already exists");
        }
        account.setAccountId(nextId());
        accounts.put(account.getAccountId(), account);
        return account.getAccountId();
    }

    /**
     * @author Josephine
     */
    public Account getAccount(String id) {
        return accounts.get(id);
    }

    /**
     * @author Josephine
     */
    public Account deleteAccount(String id){
        return accounts.remove(id);
    }

    /**
     * @author Gunn
     */
    public Boolean checkAccountExists(String id){
        return accounts.containsKey(id);
    }
}
