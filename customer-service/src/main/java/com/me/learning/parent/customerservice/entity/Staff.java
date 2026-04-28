package com.me.learning.parent.customerservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "staff")
public class Staff implements Serializable {

    @Serial
    private static final long serialVersionUID = 7049191399783887386L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "staff_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @Size (max = 45)
    @NotNull
    @Column (name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Size (max = 45)
    @NotNull
    @Column (name = "last_name", nullable = false, length = 45)
    private String lastName;

    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "address_id", nullable = false)
    private Address addressId;

    @Column (name = "picture")
    private byte[] picture;

    @Size (max = 50)
    @Column (name = "email", length = 50)
    private String email;

    @NotNull
    @ColumnDefault ("1")
    @Column (name = "active", nullable = false)
    private Boolean active;

    @Size (max = 16)
    @NotNull
    @Column (name = "username", nullable = false, length = 16)
    private String username;

    @Size (max = 40)
    @Column (name = "password", length = 40)
    private String password;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @OneToMany (mappedBy = "staff")
    private Set<Payment> payments = new LinkedHashSet<> ();

}
