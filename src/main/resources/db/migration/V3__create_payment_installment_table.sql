CREATE TABLE payment_installment (
    id BIGSERIAL PRIMARY KEY,
    installment_number INTEGER NOT NULL,
    payment_date DATE NOT NULL,
    payment_amount NUMERIC(15, 2) NOT NULL,
    interest_amount NUMERIC(15, 2) NOT NULL,
    principal_amount NUMERIC(15, 2) NOT NULL,
    remaining_balance NUMERIC(15, 2) NOT NULL,
    loan_application_id BIGINT NOT NULL,
    CONSTRAINT fk_payment_installment_loan_app FOREIGN KEY (loan_application_id) REFERENCES loan_application(id) ON DELETE CASCADE
);

CREATE INDEX idx_payment_installment_loan_app_id ON payment_installment(loan_application_id);
