CREATE TABLE verifications (
    verification_id UUID PRIMARY KEY,
    query_text VARCHAR(255) NOT NULL,
    requested_at TIMESTAMP WITH TIME ZONE NOT NULL,
    result TEXT NOT NULL,
    source VARCHAR(20) NOT NULL
);
