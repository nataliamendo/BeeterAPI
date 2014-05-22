drop user 'realm'@'localhost';
create user 'realm'@'localhost' identified by 'realm';
grant all privileges on realmdb.* to 'realm'@'localhost';
flush privileges;
