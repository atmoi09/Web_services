package account.service.dtos;

import account.service.domain.Account;

/**
 * @author Gunn
 */
public class Mapper {
    public static void mapAccountToDTO(Account account, AccountDTO accountDTO) {
        accountDTO.setAccountId(account.getAccountId());
        accountDTO.setBankAccount(account.getBankAccount());
        accountDTO.setCpr(account.getCpr());
        accountDTO.setFirstname(account.getFirstname());
        accountDTO.setLastname(account.getLastname());
    }

    public static void mapAccountDTOToAccount(AccountDTO accountDTO, Account account) {
        account.setAccountId(accountDTO.getAccountId());
        account.setBankAccount(accountDTO.getBankAccount());
        account.setCpr(accountDTO.getCpr());
        account.setFirstname(accountDTO.getFirstname());
        account.setLastname(accountDTO.getLastname());
    }
}
