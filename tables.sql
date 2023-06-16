CREATE DATABASE IF NOT EXISTS cards_db;
CREATE TABLE IF NOT EXISTS cards_db.cards (
  id int NOT NULL AUTO_INCREMENT,
  name varchar(45) NOT NULL,
  color varchar(6) DEFAULT NULL,
  status varchar(45) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  created_by INT NOT NULL,
  PRIMARY KEY (`id`)
);
CREATE TABLE IF NOT EXISTS cards_db.users (
  id INT AUTO_INCREMENT,
  email VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  role VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE INDEX email_UNIQUE (email));
  INSERT IGNORE INTO cards_db.users(id,email,password,role) VALUES(1,"test@gmail.com","1234","MEMBER") ;
  INSERT IGNORE INTO cards_db.users(id,email,password,role) VALUES(2,"test2@gmail.com","1234","ADMIN") ;
  INSERT IGNORE INTO cards_db.users(id,email,password,role) VALUES(3,"test3@gmail.com","1234","MEMBER") ;

