import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class test_password {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试数据库中的密码是否匹配"123456"
        String dbPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String plainPassword = "123456";
        
        boolean matches = encoder.matches(plainPassword, dbPassword);
        System.out.println("密码匹配结果: " + matches);
        
        // 如果密码不匹配，生成一个新的正确密码
        if (!matches) {
            String newPassword = encoder.encode(plainPassword);
            System.out.println("新的正确密码: " + newPassword);
            System.out.println("新密码长度: " + newPassword.length());
        }
    }
}