CREATE TABLE tasks (
    id VARCHAR(36) NOT NULL UNIQUE,
    tasklist_id VARCHAR(36) NOT NULL,
    title VARCHAR(100),
    version INT,
    FOREIGN KEY(tasklist_id) REFERENCES task_lists(id)
);