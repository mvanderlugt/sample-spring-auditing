package us.vanderlugt.sample.audit.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomMapping {
    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Password
    public String password(String value) {
        return passwordEncoder.encode(value);
    }
}
