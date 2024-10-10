package com.worker.remote.chunking;


import com.master.remote.chunking.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemProcessTransaction implements ItemProcessor<Transaction,Transaction>{
    @Override
    public Transaction process(Transaction transaction) throws Exception {

        System.out.println("processing transaction " + transaction);
        return transaction;
    }
}
