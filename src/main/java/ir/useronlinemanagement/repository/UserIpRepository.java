package ir.useronlinemanagement.repository;

import ir.useronlinemanagement.model.UserIp;
import ir.useronlinemanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserIpRepository extends JpaRepository<UserIp, Long> {
    List<UserIp> findByUserAndStatusTrue(User user);
//    List<UserIp> findByUserIdAndStatusTrue(Long userId);
    boolean existsByUserIdAndIpAddressAndStatusTrue(Long userId, String ipAddress);
    Long countByUserIdAndStatusTrue(Long userId);
    List<UserIp> findAllByUserIdAndStatusTrue(Long userId);
    Optional<UserIp> findFirstByIpAddress(String ipAddress);
    List<UserIp> findByUserIdAndStatusTrueOrderByRegisterDateAsc(Long userId);
}
