package us.vanderlugt.sample.audit.client;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OauthClientService implements ClientDetailsService {
    private final OauthClientRepository repository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        try {
            UUID id = UUID.fromString(clientId);
            return repository.findById(id)
                    .orElseThrow(() -> new ClientRegistrationException("Client not found " + clientId));
        } catch (Exception exception) {
            throw new ClientRegistrationException("Unable to load client " + clientId, exception);
        }
    }
}
