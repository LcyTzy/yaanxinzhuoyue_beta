ALTER TABLE product ADD COLUMN group_code VARCHAR(20) COMMENT '适配车组编码';
CREATE INDEX idx_group_code ON product (group_code);
