package com.me.learning.parent.paymentservice.entity;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "payment")
public class Payment {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "payment_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @ManyToOne (fetch = FetchType.LAZY)
    @OnDelete (action = OnDeleteAction.SET_NULL)
    @JoinColumn (name = "rental_id")
    private Rental rental;

    @NotNull
    @Column (name = "amount", nullable = false, precision = 5, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column (name = "payment_date", nullable = false)
    private Instant paymentDate;

    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update")
    private Instant lastUpdate;


}
