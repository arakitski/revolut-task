CREATE SEQUENCE revolut_test.s_account_id START WITH 1;
CREATE TABLE revolut_test.account (
 id IDENTITY NOT NULL,
 balance DECIMAL(20, 2),

 CONSTRAINT pk_account PRIMARY KEY (ID)
);