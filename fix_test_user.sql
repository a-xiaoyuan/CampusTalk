USE test;

-- 删除所有test用户
DELETE FROM db_account WHERE username = 'test';

-- 插入正确的test用户，密码是BCrypt加密的123456
INSERT INTO db_account (username, password, role) 
VALUES ('test', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'user');

-- 验证插入结果
SELECT id, username, password, role FROM db_account WHERE username = 'test';