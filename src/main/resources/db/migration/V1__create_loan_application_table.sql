CREATE TABLE loan_application (
                                  id BIGSERIAL PRIMARY KEY,
                                  first_name VARCHAR(32) NOT NULL,
                                  last_name VARCHAR(32) NOT NULL,
                                  personal_code VARCHAR(11) NOT NULL,
                                  term_months INTEGER NOT NULL,
                                  margin NUMERIC(10, 4) NOT NULL,
                                  euribor NUMERIC(10, 4) NOT NULL,
                                  amount NUMERIC(15, 2) NOT NULL,
                                  status VARCHAR(30) NOT NULL,
                                  rejection_reason VARCHAR(100),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX idx_loan_application_personal_code ON loan_application(personal_code);