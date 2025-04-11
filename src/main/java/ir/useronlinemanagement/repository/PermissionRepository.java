package ir.useronlinemanagement.repository;

import ir.useronlinemanagement.model.Permission;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByIdAndIsDeletedFalse(Long id);
    Optional<Permission> findByName(String name);

}
