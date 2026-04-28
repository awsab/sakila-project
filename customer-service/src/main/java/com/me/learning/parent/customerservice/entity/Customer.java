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
@Table (name = "customer")
public class Customer implements Serializable {

    @Serial
    private static final long serialVersionUID = -7587180278123144167L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "customer_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Size (max = 45)
    @NotNull
    @Column (name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Size (max = 45)
    @NotNull
    @Column (name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Size (max = 50)
    @Column (name = "email", length = 50)
    private String email;

    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "address_id", nullable = false)
    private Address address;

    @NotNull
    @ColumnDefault ("1")
    @Column (name = "active", nullable = false)
    private Boolean active;

    @NotNull
    @Column (name = "create_date", nullable = false)
    private Instant createDate;

    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update")
    private Instant lastUpdate;

    @OneToMany (mappedBy = "customer")
    private Set<Payment> payments = new LinkedHashSet<> ();

}
