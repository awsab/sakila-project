package com.me.learning.parent.paymentservice.entity;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "rental")
public class Rental {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "rental_id", nullable = false)
    private Integer id;

    @NotNull
    @Column (name = "rental_date", nullable = false)
    private Instant rentalDate;

    @Column (name = "return_date")
    private Instant returnDate;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @OneToMany
    private Set<Payment> payments = new LinkedHashSet<> ();

}
