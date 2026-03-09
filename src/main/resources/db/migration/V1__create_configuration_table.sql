CREATE TABLE configuration (
    id             VARCHAR(255) NOT NULL PRIMARY KEY,
    municipality_id VARCHAR(4)   NOT NULL,
    application    VARCHAR(255) NOT NULL,
    namespace      VARCHAR(255) NOT NULL,
    created_at     DATETIME(6),
    updated_at     DATETIME(6),
    UNIQUE KEY uq_configuration (municipality_id, application, namespace),
    INDEX idx_configuration_municipality_namespace (municipality_id, namespace)
);

CREATE TABLE configuration_item (
    id               VARCHAR(255) NOT NULL PRIMARY KEY,
    configuration_id VARCHAR(255) NOT NULL,
    item_key         VARCHAR(255) NOT NULL,
    item_value       VARCHAR(2000),
    FOREIGN KEY (configuration_id) REFERENCES configuration (id) ON DELETE CASCADE
);
