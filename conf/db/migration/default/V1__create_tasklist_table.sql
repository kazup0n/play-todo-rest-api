CREATE TABLE task_lists (
    id VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(100),
    version INT
);