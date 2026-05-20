


-- Question 1
CREATE TABLE Customers(first_name VARCHAR(20),last_name VARCHAR(20),birthday DATE,c_id INT, CONSTRAINT pk_customers PRIMARY KEY (c_id));
CREATE TABLE Employees(first_name VARCHAR(20),last_name VARCHAR(20),birthday DATE,e_id INT, CONSTRAINT pk_employees PRIMARY KEY (e_id));
CREATE TABLE Deals (value INT, d_id INT, CONSTRAINT pk_deals PRIMARY KEY (d_id));
CREATE TABLE TypesInDeals(d_id INT, type VARCHAR(20), CONSTRAINT pk_typeindeals PRIMARY KEY (type), CONSTRAINT fk_typeindeals foreign key (d_id) REFERENCES Deals(d_id));
CREATE TABLE Stock(amount INT,type VARCHAR(20), name VARCHAR(20), CONSTRAINT pk_stock PRIMARY KEY (name), CONSTRAINT fk_stock foreign key (type) REFERENCES TypesInDeals(type));
CREATE TABLE Transactions(date DATE,challenge BOOL,c_id INT, e_id INT, t_id INT, CONSTRAINT pk_transactions PRIMARY KEY (t_id), CONSTRAINT fk_transactions1 foreign key (c_id) REFERENCES Customers(c_id),CONSTRAINT fk_transactions2 foreign key (e_id) REFERENCES Employees(e_id));
CREATE TABLE ItemsInTransactions(name VARCHAR(20),cost INT,t_id INT, CONSTRAINT fk_itemsintransactions1 foreign key (name) REFERENCES Stock(name),CONSTRAINT fk_itemsintransactions2 foreign key (t_id) REFERENCES Transactions(t_id));

-- Question 2
INSERT INTO Customers VALUES ('Ben','Thompson','1992-07-21',6);
INSERT INTO Employees VALUES ('Rita','Davies','2000-10-07',5);
INSERT INTO Transactions VALUES ('2025-09-07',0,6,5,18);
INSERT INTO Stock VALUES (7,null,'Newspaper'),(4,null,'Pen');
INSERT INTO ItemsInTransactions VALUES ('Newspaper',149,18),('Pen',99,18);

-- Question 3
CREATE VIEW August2025SalesByDavid AS
SELECT COUNT(*) AS number_of_sales
FROM Transactions
WHERE e_id=4 AND '2025-08-01'<=date AND '2025-08-31'>=date;

-- Question 4
CREATE VIEW Above25 AS
SELECT t_id,challenge,1 as above_25
FROM Transactions NATURAL JOIN ItemsInTransactions NATURAL JOIN Stock NATURAL JOIN Customers
WHERE type='Alcohol' AND DATEDIFF(date,birthday)>=9132
UNION
SELECT t_id,challenge,0 as above_25
FROM Transactions NATURAL JOIN ItemsInTransactions NATURAL JOIN Stock NATURAL JOIN Customers
WHERE type='Alcohol' AND DATEDIFF(date,birthday)<9132;


-- Question 5
CREATE VIEW StockSold AS
SELECT name, COUNT(*) AS sold
FROM ItemsInTransactions
GROUP BY name;

CREATE VIEW StockLeft AS
SELECT name, amount-sold AS stock_left
FROM StockSold NATURAL JOIN Stock;

-- Question 6
CREATE VIEW PrizeDraw AS
SELECT *
FROM
(SELECT c_id, COUNT(*) AS number_of_transactions, 0 AS is_employee
FROM Customers C NATURAL JOIN Transactions
WHERE '2025-08-01'<=date AND '2025-08-31'>=date AND NOT( EXISTS(SELECT 1 FROM Employees E WHERE C.first_name=E.first_name AND C.last_name=E.last_name AND C.birthday=E.birthday))
GROUP BY c_id
UNION
SELECT c_id, COUNT(*) AS number_of_transactions, 1 AS is_employee
FROM Customers C NATURAL JOIN Transactions
WHERE '2025-08-01'<=date AND '2025-08-31'>=date AND EXISTS(SELECT 1 FROM Employees E WHERE C.first_name=E.first_name AND C.last_name=E.last_name AND C.birthday=E.birthday)
GROUP BY c_id) AS InnerPriceDraw
ORDER BY is_employee,number_of_transactions, c_id;



-- Question 7
CREATE VIEW ItemsWithTypes AS
SELECT *
FROM ItemsInTransactions NATURAL JOIN Stock;

CREATE VIEW NumberedItemsInTransactions AS
SELECT i1.t_id, i1.type, i1.name, COUNT(*) AS cheapest
FROM ItemsWithTypes i1, ItemsWithTypes i2
WHERE i1.t_id=i2.t_id AND i1.type=i2.type AND i1.cost>=i2.cost
GROUP BY i1.t_id, i1.type, i1.name;

-- Question 8
CREATE VIEW MaxOfType AS
SELECT t_id, type, max(cheapest) as type_count
FROM NumberedItemsInTransactions
GROUP BY t_id, type;

CREATE VIEW TypesSold AS
SELECT t_id,type,MAX(type_count) as type_count
FROM (
SELECT t_id,type,0 AS type_count
FROM Transactions,TypesInDeals
UNION 
SELECT *
FROM MaxOfType) T
GROUP BY t_id,type;

-- step 1
CREATE VIEW DealsBrought AS
SELECT t_id,d_id,MIN(type_count) AS deals_brought
FROM TypesSold NATURAL JOIN TypesInDeals
GROUP BY t_id,d_id;

-- step 2
CREATE VIEW ItemsRemaining AS
SELECT t_id,type, type_count-deals_brought AS remaining
FROM TypesSold NATURAL JOIN DealsBrought NATURAL JOIN TypesInDeals;

-- step 3
CREATE VIEW CostOfRemainderNotNull AS
SELECT t_id, sum(cost) as cost
FROM ItemsInTransactions NATURAL JOIN NumberedItemsInTransactions NATURAL JOIN ItemsRemaining
WHERE cheapest <= remaining
GROUP BY t_id;


-- step 4
CREATE VIEW CostOfRemainderNull AS
SELECT t_id,sum(cost) as cost
FROM ItemsInTransactions NATURAL JOIN Stock
WHERE type IS NULL
GROUP BY t_id;

-- step 5
CREATE VIEW CostOfDeals AS
SELECT t_id, sum(deals_brought*value) AS cost
FROM Deals NATURAL JOIN DealsBrought
GROUP BY t_id;

-- step 6
CREATE VIEW CostOfTransactions AS
SELECT t_id, sum(cost) AS cost
FROM (
SELECT *
FROM CostOfRemainderNotNull
UNION ALL
SELECT *
FROM CostOfRemainderNull
UNION ALL
SELECT *
FROM CostOfDeals) AS T
GROUP BY t_id;





