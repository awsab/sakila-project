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
@Table (name = "address")
public class Address implements Serializable {

    @Serial
    private static final long serialVersionUID = 1572185130974118311L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "address_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @Size (max = 50)
    @NotNull
    @Column (name = "address", nullable = false, length = 50)
    private String address;

    @Size (max = 50)
    @Column (name = "address2", length = 50)
    private String address2;

    @Size (max = 20)
    @NotNull
    @Column (name = "district", nullable = false, length = 20)
    private String district;

    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "city_id", nullable = false)
    private City city;

    @Size (max = 10)
    @Column (name = "postal_code", length = 10)
    private String postalCode;

    @Size (max = 20)
    @NotNull
    @Column (name = "phone", nullable = false, length = 20)
    private String phone;

    @Column (name = "location", columnDefinition = "geometry not null")
    private Object location;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @OneToMany (mappedBy = "address")
    private Set<Customer> customers = new LinkedHashSet<> ();

    @OneToMany
    private Set<Staff> staff = new LinkedHashSet<> ();

}
