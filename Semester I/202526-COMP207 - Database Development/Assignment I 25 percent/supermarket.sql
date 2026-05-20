-- BASIC RESTART
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS Employees;
-- DROP TABLE IF EXISTS Deals;
-- DROP TABLE IF EXISTS TypesInDeals;
-- DROP TABLE IF EXISTS Customers;
-- DROP TABLE IF EXISTS Transactions;
-- DROP TABLE IF EXISTS ItemsInTransactions;
-- DROP TABLE IF EXISTS Stock;
-- SET FOREIGN_KEY_CHECKS = 1;

-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 1
CREATE TABLE Customers (
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    birthday DATE,
    c_id INT,
    PRIMARY KEY (c_id)
);


CREATE TABLE Employees (
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    birthday DATE,
    e_id INT,
    PRIMARY KEY (e_id)
);


CREATE TABLE Deals (
    value INT,
    d_id INT,
    PRIMARY KEY (d_id)
);


CREATE TABLE TypesInDeals (
    d_id INT,
    type VARCHAR(20),
    PRIMARY KEY (type),
    FOREIGN KEY (d_id) REFERENCES Deals(d_id)
);


CREATE TABLE Stock (
    amount INT,
    type VARCHAR(20),
    name VARCHAR(20),
    PRIMARY KEY (name),
    FOREIGN KEY (type) REFERENCES TypesInDeals(type)
);

CREATE TABLE Transactions (
    date DATE,
    challenge BOOL,
    c_id INT,
    e_id INT,
    t_id INT,
    PRIMARY KEY (t_id),
    FOREIGN KEY (c_id) REFERENCES Customers(c_id),
    FOREIGN KEY (e_id) REFERENCES Employees(e_id)
);


CREATE TABLE ItemsInTransactions (
    name VARCHAR(20),
    cost INT,
    t_id INT,
    FOREIGN KEY (t_id) REFERENCES Transactions(t_id),
    FOREIGN KEY (name) REFERENCES Stock(name)
);
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 2
INSERT INTO Customers (first_name, last_name, birthday, c_id)
VALUES ('Ben', 'Thompson', '1992-07-21', 6);

INSERT INTO Employees (first_name, last_name, birthday, e_id)
VALUES ('Rita', 'Davies', '2000-10-07', 5);

INSERT INTO Transactions (date, challenge, c_id, e_id, t_id)
VALUES ('2025-09-07', FALSE, 6, 5, 18);

INSERT INTO Stock (amount, type, name)
VALUES 
(7, NULL, 'Newspaper'),
(4, NULL, 'Pen');

INSERT INTO ItemsInTransactions (name, cost, t_id)
VALUES 
('Newspaper', 149, 18),
('Pen', 99, 18);
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 3
CREATE OR REPLACE VIEW August2025SalesByDavid AS
SELECT COUNT(t_id) AS number_of_sales
FROM Transactions
WHERE 
    e_id = 4
    AND date BETWEEN '2025-08-01' AND '2025-08-31';

SELECT * FROM August2025SalesByDavid;
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 4
CREATE OR REPLACE VIEW Above25 AS
SELECT 
    t.t_id,
    t.challenge,
    CASE 
        WHEN DATEDIFF(t.date, c.birthday) >= 9132 THEN 1 
        ELSE 0 
    END AS above_25
FROM ItemsInTransactions i
JOIN Stock s ON i.name = s.name AND (i.name = 'wine' OR i.name = 'beer')
JOIN Transactions t ON i.t_id = t.t_id
JOIN Customers c ON t.c_id = c.c_id
GROUP BY t.t_id, t.challenge, t.date, c.birthday
ORDER BY t.t_id;

SELECT * FROM Above25 ORDER BY t_id;
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 5
CREATE OR REPLACE VIEW StockLeft AS
SELECT 
    s.name,
    s.amount - COALESCE(COUNT(i.name), 0) AS stock_left
FROM Stock s
LEFT JOIN ItemsInTransactions i ON s.name = i.name
GROUP BY s.name, s.amount
ORDER BY s.name;

SELECT * FROM StockLeft ORDER BY name;
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 6
CREATE OR REPLACE VIEW PrizeDraw AS
SELECT
    act.c_id,
    act.number_of_transactions,
    CASE
        WHEN EXISTS (
            SELECT 1
            FROM Employees e
            JOIN Customers c ON
                e.first_name = c.first_name
                AND e.last_name = c.last_name
                AND e.birthday = c.birthday
            WHERE c.c_id = act.c_id
        ) THEN 1
        ELSE 0
    END AS is_employee
FROM (
    SELECT
        c_id,
        COUNT(t_id) AS number_of_transactions
    FROM transactions
    WHERE date BETWEEN '2025-08-01' AND '2025-08-31'
    GROUP BY c_id
) act
ORDER BY is_employee, act.number_of_transactions, act.c_id;

SELECT * FROM PrizeDraw;
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 7
CREATE OR REPLACE VIEW NumberedItemsInTransactions AS
SELECT
    i.t_id,
    s.type,
    i.name,
    (
        SELECT COUNT(*) + 1
        FROM ItemsInTransactions i2
        JOIN Stock s2 ON i2.name = s2.name
        WHERE i2.t_id = i.t_id
          AND s2.type = s.type
          AND i2.cost < i.cost
          AND s2.type IS NOT NULL
    ) AS cheapest
FROM
    ItemsInTransactions i
JOIN
    Stock s ON i.name = s.name
WHERE
    s.type IS NOT NULL;

SELECT * FROM NumberedItemsInTransactions ORDER BY t_id, type, cheapest;
-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
-- QUESTION 8
CREATE OR REPLACE VIEW itemsintransactionsfull AS
SELECT 
    i.name,
    i.cost,
    i.t_id,
    s.type,
    COALESCE(td.d_id, 0) AS d_id
FROM ItemsInTransactions i
JOIN Stock s ON i.name = s.name
LEFT JOIN TypesInDeals td ON s.type = td.type;
SELECT * FROM itemsintransactionsfull ORDER BY t_id;



CREATE OR REPLACE VIEW transaction_deal_counts AS
SELECT
    t_id,
    d_id,
    CASE
        WHEN MIN(CASE WHEN type_count >= req_count THEN 1 ELSE 0 END) = 1
        THEN MIN(FLOOR(type_count / req_count))
        ELSE 0
    END AS number
FROM (
    SELECT
        tdt.t_id,
        tdt.d_id,
        tdt.type,
        tdt.req_count,
        COALESCE(ttc.type_count, 0) AS type_count
    FROM (
        SELECT
            c.t_id,
            c.d_id,
            r.type,
            r.req_count
        FROM (
            SELECT DISTINCT
                t.t_id,
                d.d_id
            FROM itemsintransactionsfull t
            CROSS JOIN (SELECT DISTINCT d_id FROM typesindeals) d
        ) c
        INNER JOIN (
            SELECT
                d_id,
                type,
                COUNT(*) AS req_count
            FROM typesindeals
            GROUP BY d_id, type
        ) r ON c.d_id = r.d_id
    ) tdt
    LEFT JOIN (
        SELECT
            t_id,
            type,
            COUNT(*) AS type_count
        FROM itemsintransactionsfull
        GROUP BY t_id, type
    ) ttc ON tdt.t_id = ttc.t_id AND tdt.type = ttc.type
) t_d_type_matched
GROUP BY t_id, d_id;








CREATE OR REPLACE VIEW costoftransaction_with_deal AS
SELECT 
    tdc.t_id,
    SUM(tdc.number * d.value) AS cost
FROM transaction_deal_counts tdc
JOIN deals d ON tdc.d_id = d.d_id
GROUP BY tdc.t_id;




SELECT * FROM costoftransaction_with_deal  ORDER BY t_id;


CREATE OR REPLACE VIEW extra_pay AS
SELECT 
    i.name,
    i.cost,
    i.t_id,
    i.type,
    i.d_id
FROM 
    itemsInTransactionsFull i
LEFT JOIN (
    SELECT 
        tdc.t_id,
        tid.type,
        tdc.number AS remove_count
    FROM 
        transaction_deal_counts tdc
    JOIN 
        typesindeals tid ON tdc.d_id = tid.d_id
    WHERE 
        tdc.number > 0
) dri 
    ON i.t_id = dri.t_id AND i.type = dri.type
WHERE 
    (
        SELECT COUNT(*) 
        FROM itemsInTransactionsFull i2 
        WHERE i2.t_id = i.t_id 
          AND i2.type = i.type 
          AND (i2.cost > i.cost OR (i2.cost = i.cost AND i2.name > i.name))
    ) + 1 > COALESCE(dri.remove_count, 0);

SELECT * FROM extra_pay;


CREATE OR REPLACE VIEW costoftransactions AS
SELECT 
    cwd.t_id,
    cwd.cost + COALESCE(ep_sum.cost, 0) AS cost
FROM costoftransaction_with_deal cwd
LEFT JOIN (
    SELECT t_id, SUM(cost) AS cost 
    FROM extra_pay 
    GROUP BY t_id
) ep_sum ON cwd.t_id = ep_sum.t_id

UNION ALL

SELECT 
    ep_sum.t_id,
    ep_sum.cost AS cost
FROM (
    SELECT t_id, SUM(cost) AS cost 
    FROM extra_pay 
    GROUP BY t_id
) ep_sum
LEFT JOIN costoftransaction_with_deal cwd ON ep_sum.t_id = cwd.t_id
WHERE cwd.t_id IS NULL;

SELECT * FROM costoftransactions ORDER BY t_id;
