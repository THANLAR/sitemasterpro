package com.sitemasterpro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RoleName name;

    @Column(length = 255)
    private String description;

    public Role() {}

    public Role(RoleName name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RoleName getName() { return name; }
    public void setName(RoleName name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public enum RoleName {
        ROLE_SUPER_ADMIN,
        ROLE_ADMIN,
        ROLE_CEO,
        ROLE_ACCOUNTANT,
        ROLE_STORE_KEEPER,
        ROLE_SITE_MANAGER,
        ROLE_SITE_ENGINEER,
        ROLE_LABOR_HEAD
    }
}
package com.sitemasterpro.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ERole name;

    public Role() {}

    public Role(ERole name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }

    public enum ERole {
        ROLE_SUPER_ADMIN,
        ROLE_ADMIN,
        ROLE_CEO,
        ROLE_ACCOUNTANT,
        ROLE_STORE_KEEPER,
        ROLE_SITE_MANAGER,
        ROLE_SITE_ENGINEER,
        ROLE_LABOR_HEAD
    }
}
