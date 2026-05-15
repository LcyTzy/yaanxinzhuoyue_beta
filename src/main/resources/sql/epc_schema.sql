-- EPC system updates

-- Add vin_prefix to vehicle_model table (ignore error if column exists)
ALTER TABLE vehicle_model ADD COLUMN vin_prefix VARCHAR(17) COMMENT 'VIN code prefix';

-- Create vehicle_part_relation table
CREATE TABLE IF NOT EXISTS vehicle_part_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_model_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    position VARCHAR(50),
    INDEX idx_vehicle_model_id (vehicle_model_id),
    INDEX idx_product_id (product_id)
);

-- Insert sample vehicle data
INSERT IGNORE INTO vehicle_brand (id, name, logo, initial, sort, status) VALUES
(1, 'Toyota', '', 'T', 1, 1),
(2, 'Honda', '', 'H', 2, 1),
(3, 'Volkswagen', '', 'D', 3, 1),
(4, 'BMW', '', 'B', 4, 1),
(5, 'Mercedes-Benz', '', 'M', 5, 1);

INSERT IGNORE INTO vehicle_series (id, brand_id, name, sort, status) VALUES
(1, 1, 'Camry', 1, 1),
(2, 1, 'Corolla', 2, 1),
(3, 2, 'Civic', 1, 1),
(4, 2, 'Accord', 2, 1),
(5, 3, 'Sagitar', 1, 1),
(6, 3, 'Lavida', 2, 1),
(7, 4, '3 Series', 1, 1),
(8, 5, 'C-Class', 1, 1);

INSERT IGNORE INTO vehicle_model (id, series_id, name, year, displacement, engine, transmission, sort, status, vin_prefix) VALUES
(1, 1, 'Camry 2023 2.5L', '2023', '2.5L', 'A25A-FKS', '8AT', 1, 1, 'LVGB'),
(2, 1, 'Camry 2022 2.0L', '2022', '2.0L', 'M20A-FKS', 'CVT', 2, 1, 'LVGC'),
(3, 2, 'Corolla 2023 1.2T', '2023', '1.2T', '9NR-FTS', 'CVT', 1, 1, 'LFM'),
(4, 3, 'Civic 2023 1.5T', '2023', '1.5T', 'L15CA', 'CVT', 1, 1, 'LHGC'),
(5, 4, 'Accord 2023 1.5T', '2023', '1.5T', 'L15BN', 'CVT', 1, 1, 'HG'),
(6, 5, 'Sagitar 2023 1.4T', '2023', '1.4T', 'EA211', '7DSG', 1, 1, 'LSV'),
(7, 6, 'Lavida 2023 1.5L', '2023', '1.5L', 'EA211', '6AT', 1, 1, 'LSV'),
(8, 7, '3 Series 2023 325Li', '2023', '2.0T', 'B48B20C', '8AT', 1, 1, 'LBV'),
(9, 8, 'C-Class 2023 C260L', '2023', '1.5T', 'M264', '9AT', 1, 1, 'LE4');

-- Insert sample vehicle-part relations
INSERT IGNORE INTO vehicle_part_relation (vehicle_model_id, product_id, position) VALUES
(1, 1, 'Engine'),
(2, 1, 'Engine'),
(3, 1, 'Engine'),
(6, 1, 'Engine'),
(7, 1, 'Engine'),
(1, 2, 'Transmission'),
(4, 2, 'Transmission'),
(5, 2, 'Transmission');
