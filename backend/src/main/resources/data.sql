INSERT INTO roles (name) VALUES ('ROLE_CUSTOMER');
INSERT INTO roles (name) VALUES ('ROLE_MANAGER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (id, username, email, password) VALUES (1, 'customer1', 'customer1@example.com', '$2a$10$Vj2Z1PIy5e5UBh.IpzdSPuKN/26uK1vZWz5cOIk6.9x8xgSFKXCNK');
INSERT INTO users (id, username, email, password) VALUES (2, 'manager1', 'manager1@example.com', '$2a$10$Vj2Z1PIy5e5UBh.IpzdSPuKN/26uK1vZWz5cOIk6.9x8xgSFKXCNK');
INSERT INTO users (id, username, email, password) VALUES (3, 'admin1', 'admin1@example.com', '$2a$10$Vj2Z1PIy5e5UBh.IpzdSPuKN/26uK1vZWz5cOIk6.9x8xgSFKXCNK');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 3);

INSERT INTO accounts (id, account_number, type, balance, created_at, user_id) VALUES (1, 'AC1000001', 'SAVINGS', 10000.00, NOW(), 1);
INSERT INTO accounts (id, account_number, type, balance, created_at, user_id) VALUES (2, 'AC1000002', 'CURRENT', 50000.00, NOW(), 1);
INSERT INTO accounts (id, account_number, type, balance, created_at, user_id) VALUES (3, 'AC2000001', 'CURRENT', 75000.00, NOW(), 2);

INSERT INTO transactions (id, account_id, transaction_type, amount, debit, transaction_date, remarks) VALUES (1, 1, 'DEPOSIT', 10000.00, false, NOW(), 'Opening deposit');
INSERT INTO transactions (id, account_id, transaction_type, amount, debit, transaction_date, remarks) VALUES (2, 2, 'DEPOSIT', 50000.00, false, NOW(), 'Opening deposit');
