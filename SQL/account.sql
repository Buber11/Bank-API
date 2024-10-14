CREATE TABLE Accounts (
    AccountNumber VARCHAR(255) PRIMARY KEY,  -- Unique bank account number
    AccountType VARCHAR(255),                 -- Type of account: savings, checking, business, etc.
    OpenDate DATE,                            -- Date the account was opened
    Balance DECIMAL(15, 2),                  -- Current balance of the account
    AccountStatus VARCHAR(255),               -- Account status: active, closed, blocked, etc.
    user_id INT,                               -- Foreign key referencing UserAccounts
    FOREIGN KEY (user_id) REFERENCES user_Accounts(user_id) -- Foreign key constraint
);

-- Table: Transactions
CREATE TABLE Transactions (
    TransactionID Serial PRIMARY KEY, -- Unique identifier for the transaction
    AccountNumber VARCHAR(255),                    -- Associated account number (foreign key)
    TransactionDate TIMESTAMP,                       -- Date and time of the transaction
    TransactionType VARCHAR(255),                  -- Type of operation: deposit, withdrawal, transfer, etc.
    Amount DECIMAL(15, 2),                         -- Amount of the transaction
    ReferenceNumber VARCHAR(255),                  -- Reference number for the transaction
    PayeePayer VARCHAR(255),                       -- Payee or payer details
    Description TEXT,                              -- Additional description of the transaction
    FOREIGN KEY (AccountNumber) REFERENCES Accounts(AccountNumber) -- Foreign key constraint
);