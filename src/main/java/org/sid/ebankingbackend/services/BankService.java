package org.sid.ebankingbackend.services;

import jakarta.transaction.Transactional;
import org.sid.ebankingbackend.entities.BankAccount;
import org.sid.ebankingbackend.entities.CurrentAccount;
import org.sid.ebankingbackend.entities.SavingAccount;
import org.sid.ebankingbackend.repositories.BanckAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankService {
    @Autowired
    private  BanckAccountRepository banckAccountRepository;

    public  void consulter(){
        BankAccount bankAccount=
                banckAccountRepository.findById("41b9c5dc-0144-44e9-96eb-6ff7dd23197f").orElse(null);
        if (bankAccount!=null){
            System.out.println("****************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            System.out.println(bankAccount.getClass().getSimpleName());
            if(bankAccount instanceof CurrentAccount){
                System.out.println("Over Draft=>"+ ((CurrentAccount)bankAccount).getOverDraft());

            }else if(bankAccount instanceof SavingAccount){
                System.out.println("Rate=>"+((SavingAccount)bankAccount).getInterestRate());
            }
            bankAccount.getAccountOperations().forEach(op->{
                System.out.println(op.getType()+"\t"+op.getOperationDate()+"\t"+op.getAmount());
            });}

    }
}
