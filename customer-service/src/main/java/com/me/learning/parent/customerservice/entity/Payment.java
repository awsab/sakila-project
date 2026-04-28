package com.me.learning.parent.customerservice.entity;

import java.io.Serial;
import java.io.Serializable;
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

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table (name = "payment")
public class Payment implements Serializable {

    @Serial
    private static final long serialVersionUID = -8912940009542481704L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "payment_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "staff_id", nullable = false)
    private Staff staff;

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
