CREATE TABLE ledger
(
    ledger_id   TEXT PRIMARY KEY,
    full_name   TEXT NOT NULL,
    created_at  TEXT NOT NULL,
    kind        INT NOT NULL,
    content     TEXT NOT NULL
);
