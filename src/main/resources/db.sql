DROP ALL OBJECTS;
CREATE TABLE IF NOT EXISTS individualuser (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	firstname VARCHAR(255) NOT NULL,
	lastname VARCHAR(255) NOT NULL,
	nationalid VARCHAR(255) UNIQUE NOT NULL,
	dateofbirth date NOT NULL,
	phonenumber VARCHAR(25) NOT NULL,
	housenumber VARCHAR(25),
	street VARCHAR(255) NOT NULL,
	town VARCHAR(255) NOT NULL,
	state VARCHAR(255) NOT NULL,
	postcode VARCHAR(255) NOT NULL,
	country VARCHAR(255) NOT NULL,
	createdat TIMESTAMP(0) NOT NULL,
	createdby VARCHAR(50) NOT NULL,
	updatedat TIMESTAMP(0),
	updatedby VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS  account (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	usertype VARCHAR(20) CHECK (usertype IN ('Person', 'Corporate')) NOT NULL,
    createdat TIMESTAMP(0) NOT NULL,
    createdby VARCHAR(50) NOT NULL,
    updatedat TIMESTAMP(0),
    updatedby VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS individualaccount (
	id BIGINT PRIMARY KEY,
	accountholder BIGINT NOT NULL,
	accountholdertype VARCHAR(20) CHECK (accountholdertype IN ('Personal', 'Joint')) NOT NULL,
	accounttype VARCHAR(20) CHECK (accounttype IN ('Current', 'Saving', 'Junior')),
	accountstatus VARCHAR(20) DEFAULT 'Active' CHECK (accountstatus IN ('Active', 'Frozen', 'Closed')) NOT NULL,
	accountbalance DECIMAL(15,2) NOT NULL,
	currency VARCHAR(10) DEFAULT 'EUR' CHECK (currency IN ('USD', 'GBP', 'EUR')) NOT NULL,
    FOREIGN KEY (accountholder) REFERENCES individualuser(id) ON DELETE CASCADE,
    FOREIGN KEY (id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jointaccount (
	id BIGINT AUTO_INCREMENT NOT NULL,
	accountid BIGINT NOT NULL,
	primaryuserid BIGINT NOT NULL,
	secondaryuserid BIGINT NOT NULL,
	createdat TIMESTAMP(0) NOT NULL,
	createdby VARCHAR(50) NOT NULL,
	updatedat TIMESTAMP(0),
	updatedby VARCHAR(50),
	PRIMARY KEY (primaryuserid, secondaryuserid),
    FOREIGN KEY (accountid) REFERENCES account(id) ON DELETE CASCADE,
    FOREIGN KEY (primaryuserid) REFERENCES individualuser(id) ON DELETE CASCADE,
    FOREIGN KEY (secondaryuserid) REFERENCES individualuser(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS parentguardian (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    minorid BIGINT NOT NULL,
    guardianid BIGINT NOT NULL,
    relationshiptype VARCHAR(20) CHECK (relationshiptype IN ('Parent', 'Guardian')) NOT NULL,
	createdat TIMESTAMP(0) NOT NULL,
	createdby VARCHAR(50) NOT NULL,
	updatedat TIMESTAMP(0),
	updatedby VARCHAR(50),
    FOREIGN KEY (minorid) REFERENCES individualuser(id) ON DELETE CASCADE,
    FOREIGN KEY (guardianid) REFERENCES individualuser(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  corporateaccount (
	id BIGINT PRIMARY KEY,
	companyname VARCHAR(20) NOT NULL,
	registrationnumber VARCHAR(30) NOT NULL,
	phonenumber VARCHAR(25) NOT NULL,
    housenumber VARCHAR(25),
    street VARCHAR(255) NOT NULL,
    town VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    postcode VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
	accountstatus VARCHAR(20) DEFAULT 'Active' CHECK (accountstatus IN ('Active', 'Frozen', 'Closed')) NOT NULL,
	accountbalance DECIMAL(15,2) NOT NULL,
	currency VARCHAR(10) DEFAULT 'EUR' CHECK (currency IN ('USD', 'GBP', 'EUR')) NOT NULL,
    FOREIGN KEY (id) REFERENCES account(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS  corporateuser (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	fullname VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
    role VARCHAR(30) CHECK (role IN ('ADMIN', 'APPROVER', 'VIEWER')) NOT NULL,
    corporateaccountid BIGINT NOT NULL,
	createdat TIMESTAMP(0) NOT NULL,
	createdby VARCHAR(50) NOT NULL,
	updatedat TIMESTAMP(0),
	updatedby VARCHAR(50),
    FOREIGN KEY (corporateaccountid) REFERENCES corporateaccount(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	accountid BIGINT NOT NULL,
	transactiontype VARCHAR(20) CHECK (transactiontype IN ('DEPOSIT', 'WITHDRAWAL')) NOT NULL,
	transactionamount FLOAT DEFAULT 0.00,
	createdat TIMESTAMP(0) NOT NULL,
	createdby VARCHAR(50) NOT NULL,
    FOREIGN KEY (accountid) REFERENCES account(id) ON DELETE CASCADE
);

INSERT INTO individualuser (firstname, lastname, nationalid, dateofbirth, phonenumber, housenumber, street, town, state, postcode, country, createdat, createdby, updatedat, updatedby) VALUES
('John', 'Doe', '123456789', '1980-01-01', '1234567890', '123', 'Main Street', 'Springfield', 'IL', '62701', 'US', TIMESTAMP '2025-02-17 11:22:20', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Jane', 'Doe', '987654321', '1985-05-12', '0987654321', '456', 'Elm Street', 'Springfield', 'IL', '62702', 'US', TIMESTAMP '2025-02-17 11:22:21', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Steve', 'Jobs', '555123456', '1955-02-24', '5555555555', '789', 'Oak Street', 'Cupertino', 'CA', '95014', 'US', TIMESTAMP '2025-02-17 11:22:22', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Steve', 'Jobs Jr', '555123487', '2010-02-24', '5555555555', '789', 'Oak Street', 'Cupertino', 'CA', '95014', 'US', TIMESTAMP '2025-02-17 11:22:22', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Adam', 'Smith', '123123487', '1995-02-24', '12325555555', '112', 'Oak Street', 'Cupertino', 'CA', '95014', 'US', TIMESTAMP '2025-02-17 11:22:22', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Adam', 'Smith', '789123487', '1999-02-24', '4567555555', '334', 'Oak Street', 'Cupertino', 'CA', '95014', 'US', TIMESTAMP '2025-02-17 11:22:22', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('John', 'Doe Jr', '223456789', '2009-01-01', '1234567890', '123', 'Main Street', 'Springfield', 'IL', '62701', 'US', TIMESTAMP '2025-02-17 11:22:20', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser');

INSERT INTO account (usertype, createdat, createdby, updatedat, updatedby) VALUES
('Person', TIMESTAMP '2025-02-17 11:22:23', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Person', TIMESTAMP '2025-02-17 11:22:24', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Person', TIMESTAMP '2025-02-17 11:22:25', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Person', TIMESTAMP '2025-02-17 11:22:26', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Person', TIMESTAMP '2025-02-17 11:22:27', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Corporate', TIMESTAMP '2025-02-17 11:22:28', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Corporate', TIMESTAMP '2025-02-17 11:22:29', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Corporate', TIMESTAMP '2025-02-17 11:22:30', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Person', TIMESTAMP '2025-02-17 11:22:27', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser'),
('Person', TIMESTAMP '2025-02-17 11:22:27', 'DBUser', TIMESTAMP '2025-02-17 11:22:23', 'DBUser');

INSERT INTO individualaccount (id, accountholder, accountholdertype, accounttype, accountstatus, accountbalance, currency) VALUES
(1, 1, 'Personal', 'Current', 'Active', 1234.56, 'USD'),
(2, 2, 'Personal', 'Current','Frozen', 1234.56, 'GBP'),
(3, 3, 'Personal', 'Saving', 'Closed', 1234.56, 'EUR'),
(4, 3, 'Joint', 'Saving', 'Active', 1234.56, 'USD'),
(5, 2, 'Joint', 'Saving', 'Active', 1234.56, 'GBP'),
(9, 4, 'Personal', 'Junior', 'Active', 123.56, 'USD'),
(10, 4, 'Personal', 'Junior', 'Active', 123.56, 'USD');

INSERT INTO jointaccount (accountid, primaryuserid, secondaryuserid, createdat, createdby, updatedat, updatedby) VALUES
(4, 3, 2, TIMESTAMP '2025-02-17 11:22:23', 'DBUser', TIMESTAMP '2025-02-17 11:22:31', 'DBUser'),
(5, 2, 1, TIMESTAMP '2025-02-17 11:22:23', 'DBUser', TIMESTAMP '2025-02-17 11:22:32', 'DBUser');

INSERT INTO parentguardian (minorid, guardianid, relationshiptype, createdat, createdby, updatedat, updatedby) VALUES
(4, 3, 'Parent', TIMESTAMP '2025-02-17 11:22:23', 'DBUser', TIMESTAMP '2025-02-17 11:22:31', 'DBUser');

INSERT INTO corporateaccount (id, companyname, registrationnumber, phonenumber, housenumber, street, town, state, postcode, country, accountstatus, accountbalance, currency) VALUES
(6, 'Pioneer Enterprises', '1122111', '1234567891', '123', 'Main Street', 'Springfield', 'IL', '62701', 'US', 'Active', 123456.78, 'USD'),
(7, 'Global Enterprises', '5566777', '2345678912', '456', 'Elm Street', 'Springfield', 'IL', '62702', 'US', 'Active', 123456.78, 'GBP'),
(8, 'Pioneer Solutions', '8877554', '3456789123', '789', 'Oak Street', 'Cupertino', 'CA', '95014', 'US', 'Frozen', 123456.78, 'EUR');

INSERT INTO corporateuser (fullname, email, role, corporateaccountid, createdat, createdby, updatedat, updatedby) VALUES
('John Doe', 'john.doe@gmail.com', 'APPROVER', 6, TIMESTAMP '2025-02-17 11:22:33', 'DBUser', NULL, NULL),
('Jane Doe', 'jane.doe@gmail.com', 'ADMIN', 7, TIMESTAMP '2025-02-17 11:22:34', 'DBUser', NULL, NULL),
('Steve Doe', 'steve.doe@gmail.com', 'VIEWER', 6, TIMESTAMP '2025-02-17 11:22:34', 'DBUser', NULL, NULL);

INSERT INTO transactions (accountid, transactiontype, transactionamount, createdat, createdby) VALUES
(1, 'DEPOSIT', 244.00, TIMESTAMP '2025-02-17 11:22:35', 'DBUser'),
(1, 'DEPOSIT', 244.00, TIMESTAMP '2025-02-17 11:22:36', 'DBUser'),
(1, 'WITHDRAWAL', 244.00, TIMESTAMP '2025-02-17 11:22:37', 'DBUser'),
(6, 'DEPOSIT', 2222244.00, TIMESTAMP '2025-02-17 11:22:35', 'DBUser');