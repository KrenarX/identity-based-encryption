package app;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
@ComponentScan("app")
public class Application {

    public static void main(String[] args) throws InvalidCipherTextException {
        SpringApplication.run(Application.class, args);
    }
}