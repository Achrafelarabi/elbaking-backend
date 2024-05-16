package org.sid.ebankingbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebankingbackend.dtos.*;
import org.sid.ebankingbackend.entities.*;
import org.sid.ebankingbackend.enums.OperationType;
import org.sid.ebankingbackend.exceptions.BalanceNotSufficentException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFounfException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;
import org.sid.ebankingbackend.mappers.BankAccountMapperImpl;
import org.sid.ebankingbackend.repositories.AccountOperationRepository;
import org.sid.ebankingbackend.repositories.BanckAccountRepository;
import org.sid.ebankingbackend.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImp  implements BankAccountService {
    private CustomerRepository customerRepository;
   private BanckAccountRepository banckAccountRepository;
   private AccountOperationRepository accountOperationRepository;
  private BankAccountMapperImpl dtoMaper;

   @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer" );
        Customer customer=dtoMaper.fromCustomerDTO(customerDTO);
        Customer savedCustomer=customerRepository.save(customer);
        return dtoMaper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount=new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
       CurrentAccount savedBankAccount = banckAccountRepository.save(currentAccount);
        return dtoMaper.fromCurrentBankAccount(savedBankAccount);

    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount=new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        SavingAccount savedBankAccount = banckAccountRepository.save(savingAccount);
        return dtoMaper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> ListCustomers() {

       List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMaper.fromCustomer(customer))
                .collect(Collectors.toList());
       /*
        List<CustomerDTO> customerDTOS=new ArrayList<>();
        for (Customer customer : customers) {
            CustomerDTO customerDTO=dtoMaper.fromCustomer(customer);
            customerDTOS.add(customerDTO);
        }
         *
         */
       return customerDTOS;
   }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFounfException {
       BankAccount bankAccount=banckAccountRepository.findById(accountId)
               .orElseThrow(()->new BankAccountNotFounfException("BankAccount not found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount =(SavingAccount) bankAccount;
            return dtoMaper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount= (CurrentAccount) bankAccount;
            return dtoMaper.fromCurrentBankAccount (currentAccount);
        }

    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFounfException, BalanceNotSufficentException {
        BankAccount bankAccount=banckAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFounfException("BankAccount not found"));
      if (bankAccount.getBalance()<amount)
          throw  new BalanceNotSufficentException("Balance not sufficient");
        AccountOperation accountOperation=new AccountOperation();
       accountOperation.setType(OperationType.DEBIT);
       accountOperation.setAmount(amount);
       accountOperation.setDescription(description);
       accountOperation.setOperationDate(new Date());
       accountOperation.setBankAccount(bankAccount);
       accountOperationRepository.save(accountOperation);
      bankAccount.setBalance(bankAccount.getBalance()-amount);
      banckAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFounfException {
        BankAccount bankAccount=banckAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFounfException("BankAccount not found"));
        AccountOperation accountOperation=new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        banckAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFounfException, BalanceNotSufficentException {
      debit(accountIdSource,amount,"Transfer to"+accountIdDestination);
      credit(accountIdDestination,amount,"Transfer from"+accountIdSource);
    }
   @Override
   public List<BankAccountDTO>bankAccountList(){
       List<BankAccount> bankAccounts = banckAccountRepository.findAll();
       List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
           if (bankAccount instanceof SavingAccount) {
               SavingAccount savingAccount = (SavingAccount) bankAccount;
               return dtoMaper.fromSavingBankAccount(savingAccount);
           } else {
               CurrentAccount currentAccount = (CurrentAccount) bankAccount;
               return dtoMaper.fromCurrentBankAccount(currentAccount);
           }
       }).collect(Collectors.toList());
       return bankAccountDTOS;
   }
    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return  dtoMaper.fromCustomer(customer);

    }
    @Override
    public CustomerDTO updatCustomer(CustomerDTO customerDTO) {
        log.info("Saving new customer" );
        Customer customer=dtoMaper.fromCustomerDTO(customerDTO);
        Customer savedCustomer=customerRepository.save(customer);
        return dtoMaper.fromCustomer(savedCustomer);
    }
    @Override
     public  void deleteCustomer(Long customerId) {
       customerRepository.deleteById(customerId);
     }
     @Override
public  List<AccountOperationDTO> accountHistory(String accountId){
    List<AccountOperation> accountOperations =
            accountOperationRepository.findByBankAccountId(accountId);
    return accountOperations.stream().map(op->dtoMaper.fromAccountOperation(op)).collect(Collectors.toList());


}

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFounfException {
       BankAccount bankAccount=banckAccountRepository.findById(accountId).orElse(null);
       if (bankAccount==null)throw new BankAccountNotFounfException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
       AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> dtoMaper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

}
