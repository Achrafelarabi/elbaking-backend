package org.sid.ebankingbackend.services;

import org.sid.ebankingbackend.dtos.*;
import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.CurrentAccount;
import org.sid.ebankingbackend.entities.Customer;
import org.sid.ebankingbackend.entities.SavingAccount;
import org.sid.ebankingbackend.exceptions.BalanceNotSufficentException;
import org.sid.ebankingbackend.exceptions.BankAccountNotFounfException;
import org.sid.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
     CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft , Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate , Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO>ListCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFounfException;
    void debit(String accountId, double amount,String description) throws BankAccountNotFounfException, BalanceNotSufficentException;
    void credit(String accountId, double amount,String description) throws BankAccountNotFounfException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFounfException, BalanceNotSufficentException;


    List<BankAccountDTO>bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updatCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFounfException;

}
