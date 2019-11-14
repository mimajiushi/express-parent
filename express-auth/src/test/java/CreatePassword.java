import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreatePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String expressApp = bCryptPasswordEncoder.encode("ExpressApp");
        System.out.println(expressApp);
    }
}
