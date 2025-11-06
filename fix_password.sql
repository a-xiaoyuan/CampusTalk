USE test;

-- 删除所有现有的test用户
DELETE FROM db_account WHERE username = 'test';

-- 插入test用户，使用BCrypt加密的密码（123456）
-- 注意：使用CHAR()函数来避免特殊字符问题
INSERT INTO db_account (username, password, role) 
VALUES ('test', 
    CONCAT(
        CHAR(36), '2a', CHAR(36), '10', CHAR(36), 'N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
    ), 
    'user'
);

-- 验证插入结果
SELECT id, username, password, LENGTH(password) as password_length 
FROM db_account 
WHERE username = 'test';