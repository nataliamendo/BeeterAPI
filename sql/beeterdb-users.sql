drop user 'beeter'@'localhost';
create user 'beeter'@'localhost' identified by 'beeter';
grant all privileges on beeterdb.* to 'beeter'@'localhost';
flush privileges;
