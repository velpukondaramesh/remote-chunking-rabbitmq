package com.master.remote.chunking;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.batch.core.Job;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.ChunkMessageChannelItemWriter;
import org.springframework.batch.integration.chunk.RemoteChunkHandlerFactoryBean;
import org.springframework.batch.integration.chunk.RemoteChunkingManagerStepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.dsl.IntegrationFlow;

import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RemoteChunkingConfiguration {


    /*private final JobBuilderFactory jobBuilderFactory;
    private final RemoteChunkingManagerStepBuilderFactory remoteChunkingMaster;
    private final ItemReader<Transaction> itemReader;
    private final CustomItemProcessListener customItemProcessListener;*/

    @Bean
    public DirectChannel requests() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
        return IntegrationFlow.from("requests")
                .handle(Amqp.outboundAdapter(amqpTemplate)
                        .routingKey("requests"))
                .get();
    }

    @Bean
    public MessagingTemplate messagingTemplate() {
        MessagingTemplate template = new MessagingTemplate();
        template.setDefaultChannel(requests());
        template.setReceiveTimeout(2000);
        return template;
    }

    @Bean
    @StepScope
    public ChunkMessageChannelItemWriter<Transaction> itemWriter() {
        ChunkMessageChannelItemWriter<Transaction> chunkMessageChannelItemWriter =
                new ChunkMessageChannelItemWriter<>();
        chunkMessageChannelItemWriter.setMessagingOperations(messagingTemplate());
        chunkMessageChannelItemWriter.setReplyChannel(replies());
        return chunkMessageChannelItemWriter;
    }

    @Bean
    public RemoteChunkHandlerFactoryBean<Transaction> chunkHandler() {
        RemoteChunkHandlerFactoryBean<Transaction> remoteChunkHandlerFactoryBean = new RemoteChunkHandlerFactoryBean<>();
        remoteChunkHandlerFactoryBean.setChunkWriter(itemWriter());
        remoteChunkHandlerFactoryBean.setStep(step1(null, null));
        return remoteChunkHandlerFactoryBean;
    }

    @Bean
    public QueueChannel replies() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow replyFlow(ConnectionFactory connectionFactory) {
        return IntegrationFlow
                .from(Amqp.inboundAdapter(connectionFactory, "replies"))
                .channel(replies())
                .get();
    }

    @Bean
    public FlatFileItemReader<Transaction> fileTransactionReader() {

        return new FlatFileItemReaderBuilder<Transaction>()
                .saveState(false)
                .resource(new ClassPathResource("/csv-file/transactions.csv"))
                .delimited()
                .names(new String[] {"account", "amount", "timestamp"})
                .fieldSetMapper(fieldSet -> {
                    Transaction transaction = new Transaction();

                    transaction.setAccount(fieldSet.readString("account"));
                    transaction.setAmount(fieldSet.readString("amount"));
                    transaction.setTimestamp(fieldSet.readString("timestamp"));

                    return transaction;
                })
                .build();
    }

    @Bean
    public TaskletStep step1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("step1", jobRepository)
                .<Transaction, Transaction>chunk(100, transactionManager)
                .reader(fileTransactionReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job remoteChunkingJob(JobRepository jobRepository) {
        return new JobBuilder("remoteChunkingJob", jobRepository)
                .start(step1(null, null))
                .build();
    }
}
