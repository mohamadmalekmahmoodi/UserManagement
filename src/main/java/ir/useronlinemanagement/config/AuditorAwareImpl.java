package ir.useronlinemanagement.config;

import ir.useronlinemanagement.controller.response.UserDetailsRes;
import ir.useronlinemanagement.service.UserService;
import jakarta.validation.constraints.NotNull;
import jdk.jshell.execution.LoaderDelegate;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    private final UserService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditorAwareImpl.class);

    @Autowired
    public AuditorAwareImpl(UserService service) {
        this.service = service;
    }

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        try {
            UserDetailsRes userDetails = service.getDetails();
            if (userDetails != null && userDetails.getUsername() != null) {
                return Optional.of(userDetails.getUsername());
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while getting current auditor", e);
        }
        return Optional.empty();
    }
}
