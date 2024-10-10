# remot-chunking-spring-batch-integration-rabbitmq

1.please run rebbitmq on docker with under commande: 

1.1.docker pull rabbitmq:3-management

1.2.docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3-management

***if you want to run rabbitMQ once and for all time:

sudo docker run -d -p 15672:15672 -p 5672:5672 --restart=always --name RabbitMQ rabbitmq:3-management


2.login in rabbitMQ with username and password : quest  

3.you should create two queues in rabbitMQ with names: replies and requests

4.you should create database "remote-chunk" in sqlserver

5.and create table "info" with sql code :

CREATE TABLE IF NOT EXISTS info (
id bigint primary key,
account varchar(50) ,
amount varchar(50) ,
timestamp varchar(50)
)

6.run master-remote-chunking

7.run worker-remote-chunking

8.run the job with:
8.1.http://localhost:8000/load

9.finish
  
