-- Insert roles
INSERT INTO roles (name, description, created_at, updated_at) VALUES
('SUPER_ADMIN', 'Full system access, user management, high-value approvals', NOW(), NOW()),
('ADMIN', 'Multi-project oversight, mid-level expenditure approval', NOW(), NOW()),
('CEO', 'Executive dashboards, strategic decision making', NOW(), NOW()),
('ACCOUNTANT', 'Financial transaction management, budget tracking', NOW(), NOW()),
('STORE_KEEPER', 'Inventory management, material tracking', NOW(), NOW()),
('SITE_MANAGER', 'On-site operations, progress updates', NOW(), NOW()),
('SITE_ENGINEER', 'Technical execution, quality control', NOW(), NOW()),
('LABOR_HEAD', 'Workforce management, attendance tracking', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Insert default super admin user (password: admin123)
INSERT INTO users (username, email, password, full_name, phone_number, is_active, created_at, updated_at) VALUES
('admin', 'admin@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'System Administrator', '+1234567890', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Assign super admin role to default user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

-- Insert sample users for each role
INSERT INTO users (username, email, password, full_name, phone_number, is_active, created_at, updated_at) VALUES
('ceo', 'ceo@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'Chief Executive Officer', '+1234567891', true, NOW(), NOW()),
('accountant', 'accountant@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'Company Accountant', '+1234567892', true, NOW(), NOW()),
('storekeeper', 'storekeeper@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'Store Keeper', '+1234567893', true, NOW(), NOW()),
('sitemanager', 'sitemanager@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'Site Manager', '+1234567894', true, NOW(), NOW()),
('engineer', 'engineer@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'Site Engineer', '+1234567895', true, NOW(), NOW()),
('laborhead', 'laborhead@sitemasterpro.com', '$2a$10$rqXX8j8lXqXuVqRqXX8j8uXqXuVqRqXX8j8lXqXuVqRqXX8j8lXqXu', 'Labor Head', '+1234567896', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Assign roles to users
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'ceo' AND r.name = 'CEO'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'accountant' AND r.name = 'ACCOUNTANT'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'storekeeper' AND r.name = 'STORE_KEEPER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'sitemanager' AND r.name = 'SITE_MANAGER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'engineer' AND r.name = 'SITE_ENGINEER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'laborhead' AND r.name = 'LABOR_HEAD'
ON CONFLICT DO NOTHING;

-- Insert sample suppliers
INSERT INTO suppliers (name, contact_person, email, phone_number, address, tax_number, payment_terms, is_active, created_at, updated_at) VALUES
('ABC Construction Supply', 'John Smith', 'john@abcsupply.com', '+1555-0101', '123 Supply St, Construction City, CC 12345', 'TAX123456789', 'NET 30', true, NOW(), NOW()),
('XYZ Materials Corp', 'Jane Doe', 'jane@xyzmaterials.com', '+1555-0102', '456 Material Ave, Building Town, BT 67890', 'TAX987654321', 'NET 15', true, NOW(), NOW()),
('Premier Building Supplies', 'Bob Johnson', 'bob@premier.com', '+1555-0103', '789 Builder Blvd, Supply City, SC 11111', 'TAX555666777', 'NET 45', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Insert sample projects
INSERT INTO projects (name, description, project_code, status, start_date, planned_end_date, budget, projected_revenue, client_name, client_contact, location, created_at, updated_at) VALUES
('Downtown Office Complex', 'Construction of a 15-story office building in downtown area', 'DOC-2024-001', 'ACTIVE', '2024-01-15', '2024-12-31', 5000000.00, 6500000.00, 'Metro Development Corp', 'Sarah Wilson (+1555-2001)', 'Downtown Metro City', NOW(), NOW()),
('Residential Housing Project', 'Development of 50-unit residential complex with amenities', 'RHP-2024-002', 'PLANNING', '2024-03-01', '2025-02-28', 3200000.00, 4000000.00, 'Green Valley Homes', 'Michael Brown (+1555-2002)', 'Green Valley Suburbs', NOW(), NOW()),
('Industrial Warehouse', 'Construction of 100,000 sq ft warehouse facility', 'IW-2024-003', 'ACTIVE', '2024-02-01', '2024-10-15', 2800000.00, 3500000.00, 'Logistics Solutions Inc', 'David Lee (+1555-2003)', 'Industrial Park Zone', NOW(), NOW())
ON CONFLICT (project_code) DO NOTHING;

-- Insert sample materials
INSERT INTO materials (name, description, sku, unit, unit_price, current_stock, minimum_stock, maximum_stock, reorder_point, is_active, supplier_id, created_at, updated_at) VALUES
('Portland Cement', 'Type I Portland Cement, 50kg bags', 'CEM-001', 'bag', 12.50, 500.0, 100.0, 1000.0, 150.0, true, 
    (SELECT id FROM suppliers WHERE name = 'ABC Construction Supply' LIMIT 1), NOW(), NOW()),
('Steel Rebar #4', '12mm diameter steel reinforcement bars, 6m length', 'REB-004', 'piece', 25.75, 200.0, 50.0, 500.0, 75.0, true,
    (SELECT id FROM suppliers WHERE name = 'XYZ Materials Corp' LIMIT 1), NOW(), NOW()),
('Concrete Blocks', '8x8x16 inch concrete masonry units', 'BLK-816', 'piece', 3.25, 1000.0, 200.0, 2000.0, 300.0, true,
    (SELECT id FROM suppliers WHERE name = 'ABC Construction Supply' LIMIT 1), NOW(), NOW()),
('Ready Mix Concrete', 'M25 grade ready mix concrete', 'RMC-M25', 'cubic meter', 85.00, 0.0, 0.0, 100.0, 10.0, true,
    (SELECT id FROM suppliers WHERE name = 'Premier Building Supplies' LIMIT 1), NOW(), NOW()),
('Plywood Sheets', '18mm Marine grade plywood, 8x4 feet', 'PLY-184', 'sheet', 45.00, 150.0, 30.0, 300.0, 50.0, true,
    (SELECT id FROM suppliers WHERE name = 'XYZ Materials Corp' LIMIT 1), NOW(), NOW())
ON CONFLICT (sku) DO NOTHING;

-- Sample audit log (for demonstration)
INSERT INTO audit_logs (user_id, action, entity_type, entity_id, new_value, timestamp) VALUES
((SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'SYSTEM_INIT', 'SYSTEM', null, 'Initial system setup completed', NOW());
