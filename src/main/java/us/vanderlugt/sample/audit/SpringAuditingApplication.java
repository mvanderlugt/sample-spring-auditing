package us.vanderlugt.sample.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@EnableJpaAuditing
@EnableSpringDataWebSupport
@SpringBootApplication
public class SpringAuditingApplication {
    @Value("${password.strength:10}")
    @Min(10) @Max(31)
    private Integer passwordStrength;

    public static void main(String[] args) {
        SpringApplication.run(SpringAuditingApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordStrength);
    }
}
