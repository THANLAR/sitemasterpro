-- Insert default roles
INSERT INTO roles (name, description) VALUES 
    ('ROLE_SUPER_ADMIN', 'Super Administrator with full system access'),
    ('ROLE_ADMIN', 'Administrator with multi-project oversight'),
    ('ROLE_CEO', 'Chief Executive Officer with executive-level access'),
    ('ROLE_ACCOUNTANT', 'Accountant with financial transaction management'),
    ('ROLE_STORE_KEEPER', 'Store Keeper with inventory management access'),
    ('ROLE_SITE_MANAGER', 'Site Manager with on-site operations access'),
    ('ROLE_SITE_ENGINEER', 'Site Engineer with technical execution access'),
    ('ROLE_LABOR_HEAD', 'Labor Head with workforce management access')
ON CONFLICT (name) DO NOTHING;

-- Insert default super admin user (password: admin123)
INSERT INTO users (username, password, email, full_name, enabled) VALUES 
    ('admin', '$2a$10$pCO8k4YjZKuSkzuLWU1Qs.IhtSdVdZv4qHNbwKFcpwFkZFh.ZOsn2', 'admin@sitemasterpro.com', 'System Administrator', true)
ON CONFLICT (username) DO NOTHING;

-- Assign super admin role to admin user
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id 
FROM users u, roles r 
WHERE u.username = 'admin' AND r.name = 'ROLE_SUPER_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Insert sample projects for demonstration
INSERT INTO projects (name, description, location, start_date, end_date, contract_value, budgeted_cost, status) VALUES 
    ('Metro Bridge Construction', 'Construction of 2.5km metro bridge over river', 'Downtown Metro Line', '2024-01-15', '2024-12-31', 15000000.00, 12000000.00, 'IN_PROGRESS'),
    ('Highway Expansion Project', '4-lane highway expansion with 3 major overpasses', 'State Highway 101', '2024-03-01', '2025-02-28', 25000000.00, 20000000.00, 'PLANNING'),
    ('Commercial Complex Foundation', 'Foundation work for 25-story commercial complex', 'Business District', '2024-02-01', '2024-08-31', 8500000.00, 7200000.00, 'IN_PROGRESS')
ON CONFLICT DO NOTHING;

-- Insert sample materials
INSERT INTO materials (name, description, unit, unit_price, current_stock, min_stock_level, max_stock_level) VALUES 
    ('Portland Cement', 'Grade 43 Portland Cement for construction', 'Bags (50kg)', 350.00, 1500.00, 200.00, 5000.00),
    ('Steel Rebar 12mm', '12mm diameter steel reinforcement bars', 'Tonnes', 45000.00, 25.50, 5.00, 100.00),
    ('Concrete Blocks', 'Standard concrete blocks for construction', 'Pieces', 45.00, 2500.00, 500.00, 10000.00),
    ('Sand (River)', 'Fine river sand for concrete mixing', 'Cubic Meters', 850.00, 150.75, 25.00, 500.00),
    ('Gravel 20mm', '20mm graded gravel for concrete', 'Cubic Meters', 1200.00, 85.25, 15.00, 300.00)
ON CONFLICT DO NOTHING;

-- Insert sample suppliers
INSERT INTO suppliers (name, address, phone_number, email, contact_person) VALUES 
    ('ABC Construction Materials', '123 Industrial Area, Sector 15', '+91-9876543210', 'sales@abcmaterials.com', 'Rajesh Kumar'),
    ('Metro Steel Corporation', '45 Steel Market, Industrial Zone', '+91-9876543211', 'orders@metrosteel.com', 'Priya Singh'),
    ('Quality Cement Ltd', '78 Cement Road, Manufacturing Hub', '+91-9876543212', 'supply@qualitycement.com', 'Amit Sharma'),
    ('BuildTech Suppliers', '90 Builder Street, Trade Center', '+91-9876543213', 'info@buildtech.com', 'Sunita Patel')
ON CONFLICT DO NOTHING;
