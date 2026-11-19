CREATE TABLE registration_requests (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    username VARCHAR(80) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone_number VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by VARCHAR(80),
    reviewed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_registration_request_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'))
);

-- Only one pending request may exist for a username or email at a time.
CREATE UNIQUE INDEX uk_registration_pending_username_ci
    ON registration_requests (LOWER(username)) WHERE status = 'PENDING';
CREATE UNIQUE INDEX uk_registration_pending_email_ci
    ON registration_requests (LOWER(email)) WHERE status = 'PENDING';
CREATE INDEX idx_registration_requests_status_created
    ON registration_requests (status, created_at);

-- Enforce case-insensitive uniqueness for login identities, including concurrent requests.
CREATE UNIQUE INDEX uk_users_username_ci ON users (LOWER(username));
CREATE UNIQUE INDEX uk_users_email_ci ON users (LOWER(email));
