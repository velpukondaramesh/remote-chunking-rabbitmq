package com.worker.remote.chunking;


import com.master.remote.chunking.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ItemWriterTransaction {

    public DataSource dataSource(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSourceBuilder.url("jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=remote-chunking;");
        dataSourceBuilder.username("admin");
        dataSourceBuilder.password("Pdfprimerplus@007");
        return dataSourceBuilder.build();
    }

    public static String INSERT_TRANSACTION_SQL =
            "INSERT INTO INFO (ACCOUNT,AMOUNT,TIMESTAMP) VALUES (:account,:amount,:timestamp)";

    @Bean
    public ItemWriter<Transaction> itemWriterTransactionBean() {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource())
                .sql(INSERT_TRANSACTION_SQL)
                .beanMapped()
                .build();
    }

}
