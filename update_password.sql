USE test;

-- 更新test用户的密码为正确的BCrypt哈希值（对应明文123456）
UPDATE db_account 
SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' 
WHERE username = 'test';

-- 验证更新结果
SELECT id, username, LENGTH(password) as password_length, password 
FROM db_account 
WHERE username = 'test';