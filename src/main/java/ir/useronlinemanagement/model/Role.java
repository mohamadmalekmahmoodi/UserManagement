package ir.useronlinemanagement.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Table(name = "TB_ROLE")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @ManyToMany(mappedBy = "roles")
    @JsonManagedReference
    private List<User> users = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER) // Many-to-many relationship with Permission
    @JoinTable(
            name = "role_permission", // Join table name
            joinColumns = @JoinColumn(name = "role_id"), // Role's join column
            inverseJoinColumns = @JoinColumn(name = "permission_id") // Permission's join column
    )
    private List<Permission> permissions;

    public String getDescription() {
        return description;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    // Permissions associated with this role

    public List<Permission> getPermissions() {
        return permissions; // Getter method for permissions
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public Role(String name) {
        this.name = name;
    }

    public Role() {
    }

}
