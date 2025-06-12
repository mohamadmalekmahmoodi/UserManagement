package ir.useronlinemanagement.repository;

import ir.useronlinemanagement.model.IpVerification;
import ir.useronlinemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IpVerificationRepository extends JpaRepository<IpVerification, Long> {
    Optional<IpVerification> findByIpAddressAndOtpCode(String ip, String code);
}

