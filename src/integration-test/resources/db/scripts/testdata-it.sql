INSERT INTO configuration (id, municipality_id, application, namespace, created_at, updated_at)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '2281', 'test-app', 'feature-flags', '2024-01-01 12:00:00.000000', '2024-01-02 12:00:00.000000');

INSERT INTO configuration_item (id, configuration_id, item_key, item_value) VALUES ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'dark-mode', 'true');
INSERT INTO configuration_item (id, configuration_id, item_key, item_value) VALUES ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'beta-ui', 'false');
INSERT INTO configuration_item (id, configuration_id, item_key, item_value) VALUES ('33333333-3333-3333-3333-333333333333', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'allowed-roles', 'admin');
INSERT INTO configuration_item (id, configuration_id, item_key, item_value) VALUES ('44444444-4444-4444-4444-444444444444', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'allowed-roles', 'editor');

INSERT INTO configuration (id, municipality_id, application, namespace, created_at, updated_at)
VALUES ('b2c3d4e5-f6a7-8901-bcde-f12345678901', '2281', 'test-app', 'settings', '2024-02-01 12:00:00.000000', '2024-02-02 12:00:00.000000');

INSERT INTO configuration_item (id, configuration_id, item_key, item_value) VALUES ('55555555-5555-5555-5555-555555555555', 'b2c3d4e5-f6a7-8901-bcde-f12345678901', 'timeout', '30');

INSERT INTO configuration (id, municipality_id, application, namespace, created_at, updated_at)
VALUES ('c3d4e5f6-a7b8-9012-cdef-123456789012', '2281', 'other-app', 'defaults', '2024-03-01 12:00:00.000000', '2024-03-02 12:00:00.000000');

INSERT INTO configuration_item (id, configuration_id, item_key, item_value) VALUES ('66666666-6666-6666-6666-666666666666', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'color', 'blue');
