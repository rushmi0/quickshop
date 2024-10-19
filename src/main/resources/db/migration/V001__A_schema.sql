CREATE TABLE ledger
(
    ledger_id   SERIAL PRIMARY KEY,
    nick_name  VARCHAR(64) NOT NULL,
    created_at VARCHAR(64) NOT NULL,
    kind       INT         NOT NULL,
    content    TEXT        NOT NULL,
);