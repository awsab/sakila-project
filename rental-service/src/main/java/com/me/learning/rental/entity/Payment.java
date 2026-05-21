package com.me.learning.rental.entity;

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
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * Records a single financial transaction against a rental.
 *
 * <p><b>Cross-service references (stored as plain IDs):</b>
 * <ul>
 *   <li>{@code customerId} — references {@code customer.customer_id} owned
 *       by <em>customer-service</em>.</li>
 *   <li>{@code staffId}    — references {@code staff.staff_id} owned
 *       by <em>customer-service</em>.</li>
 * </ul>
 *
 * <p>{@code rental} is an intra-service FK — both {@code payment} and
 * {@code rental} tables live in this service's schema, so a real JPA
 * {@code @ManyToOne} relationship is used.  A payment can theoretically
 * exist without a rental (e.g. a deposit), so the FK is nullable.
 *
 * Maps to the {@code payment} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "payment")
public class Payment {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "payment_id", columnDefinition = "smallint UNSIGNED not null")
    private Integer id;

    /**
     * ID of the customer who made the payment.
     * Cross-service reference — {@code customer} is owned by
     * <em>customer-service</em>.
     */
    @NotNull
    @Column (name = "customer_id", nullable = false,
            columnDefinition = "smallint UNSIGNED not null")
    private Integer customerId;

    /**
     * ID of the staff member who received the payment.
     * Cross-service reference — {@code staff} is owned by
     * <em>customer-service</em>.
     */
    @NotNull
    @Column (name = "staff_id", nullable = false,
            columnDefinition = "tinyint UNSIGNED not null")
    private Short staffId;

    /**
     * The rental this payment covers.
     * Intra-service FK — {@code rental} is owned by this service.
     * Nullable: a payment can exist without a linked rental (e.g. deposits).
     */
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "rental_id")
    private Rental rental;

    /**
     * The amount charged for this payment.
     * Corresponds to DECIMAL(5,2) — up to 999.99.
     */
    @NotNull
    @Digits (integer = 3, fraction = 2)
    @Column (name = "amount", nullable = false, precision = 5, scale = 2)
    private BigDecimal amount;

    /**
     * Date and time the payment was made.
     * Stored as an {@link Instant} (maps to UTC; Sakila's DATETIME column).
     */
    @NotNull
    @Column (name = "payment_date", nullable = false)
    private Instant paymentDate;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

}

