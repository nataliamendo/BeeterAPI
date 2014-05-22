drop database if exists beeterdb;
create database beeterdb;
 
use beeterdb;
 
create table users (
username	varchar(20) not null primary key,
name	varchar(70) not null,
email	varchar(255) not null
);
 
create table stings (
stingid int not null auto_increment primary key,
username varchar(20) not null,
subject	varchar(100) not null,
content	varchar(500) not null,
last_modified	timestamp,
foreign key(username) references users(username)
);