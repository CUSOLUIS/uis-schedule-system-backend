-- Insert Roles
INSERT INTO roles (role_guid, name, is_active, created_at, updated_at) VALUES 
('11111111-1111-1111-1111-111111111111', 'ADMINISTRADOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('22222222-2222-2222-2222-222222222222', 'OPERADOR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('33333333-3333-3333-3333-333333333333', 'DOCENTE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('44444444-4444-4444-4444-444444444444', 'ESTUDIANTE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Users
-- Passwords are set to '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIvi' which corresponds to '1234' with BCrypt
INSERT INTO users (user_id, email, password, first_name, last_name, username, is_enabled, account_no_expired, account_no_locked, credential_no_expired) VALUES 
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'nicole0202@uis.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIvi', 'Nicole', 'Alvarez', 'nalvarez', true, true, true, true),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'marcos123@uis.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIvi', 'Marcos', 'Perez', 'mperez', true, true, true, true),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'dayanna123@uis.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIvi', 'Dayanna', 'Diaz', 'ddiaz', true, true, true, true),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'julian123@uis.edu.co', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIvi', 'Julian', 'Gomez', 'jgomez', true, true, true, true);

-- Link Users to Roles
INSERT INTO user_roles (user_id, role_guid) VALUES 
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111'), -- Nicole (ADMINISTRADOR)
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '44444444-4444-4444-4444-444444444444'), -- Marcos (ESTUDIANTE)
('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222'), -- Dayanna (OPERADOR)
('dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333'); -- Julian (DOCENTE)
