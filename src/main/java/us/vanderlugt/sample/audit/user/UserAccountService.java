/*
 * Copyright 2019 Mark Vander Lugt
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package us.vanderlugt.sample.audit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
@RequiredArgsConstructor
public class UserAccountService implements UserDetailsService {
    private final UserAccountRepository repository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LocalDateTime now = now(ZoneId.of("UTC"));
        return repository.findByUsernameIgnoreCase(username)
                .map(u -> new User(u.getUsername(), u.getPassword(),
                        u.getDisabled() == null,
                        u.getExpires() == null || u.getExpires().isAfter(now),
                        u.getPasswordExpires() == null || u.getPasswordExpires().isAfter(now),
                        u.getLocked() == null,
                        u.getRoles().stream()
                                .map(r -> new SimpleGrantedAuthority(r.getCode()))
                                .collect(Collectors.toSet())))
                .orElseThrow(() -> new UsernameNotFoundException("Unable to find user " + username));
    }
}
