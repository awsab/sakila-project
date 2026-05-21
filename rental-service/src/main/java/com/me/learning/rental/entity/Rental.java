package com.me.learning.rental.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


/**
 * Records a single rental transaction — which customer took which inventory
 * disc, from which staff member, and when.
 *
 * <p><b>Cross-service references (stored as plain IDs):</b>
 * <ul>
 *   <li>{@code inventoryId} — references {@code inventory.inventory_id} owned
 *       by <em>inventory-service</em>.</li>
 *   <li>{@code customerId}  — references {@code customer.customer_id} owned
 *       by <em>customer-service</em>.</li>
 *   <li>{@code staffId}     — references {@code staff.staff_id} owned
 *       by <em>customer-service</em>.</li>
 * </ul>
 * No JPA joins are created across service boundaries; foreign data is resolved
 * at runtime via service-to-service calls.
 *
 * <p>A unique constraint on {@code (rental_date, inventory_id, customer_id)}
 * prevents duplicate rental records for the same disc on the same date.
 *
 * Maps to the {@code rental} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (
        name = "rental",
        uniqueConstraints = @UniqueConstraint (
                name = "rental_date",
                columnNames = {"rental_date", "inventory_id", "customer_id"}
        )
)
public class Rental {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "rental_id", columnDefinition = "int not null")
    private Integer id;

    /**
     * Date and time the disc was rented out.
     * Stored as an {@link Instant} (maps to UTC; Sakila's DATETIME column).
     */
    @NotNull
    @Column (name = "rental_date", nullable = false)
    private Instant rentalDate;

    /**
     * ID of the specific inventory disc that was rented.
     * Cross-service reference — {@code inventory} is owned by
     * <em>inventory-service</em>.
     */
    @NotNull
    @Column (name = "inventory_id", nullable = false,
            columnDefinition = "mediumint UNSIGNED not null")
    private Integer inventoryId;

    /**
     * ID of the customer who rented the disc.
     * Cross-service reference — {@code customer} is owned by
     * <em>customer-service</em>.
     */
    @NotNull
    @Column (name = "customer_id", nullable = false,
            columnDefinition = "smallint UNSIGNED not null")
    private Integer customerId;

    /**
     * Date and time the disc was returned; {@code null} until returned.
     * Stored as an {@link Instant}.
     */
    @Column (name = "return_date")
    private Instant returnDate;

    /**
     * ID of the staff member who processed the rental.
     * Cross-service reference — {@code staff} is owned by
     * <em>customer-service</em>.
     */
    @NotNull
    @Column (name = "staff_id", nullable = false,
            columnDefinition = "tinyint UNSIGNED not null")
    private Short staffId;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    /** All payment records made against this rental. */
    @OneToMany (mappedBy = "rental")
    private Set<Payment> payments = new LinkedHashSet<> ();

    // ── Derived convenience ───────────────────────────────────────────────────

    /**
     * Calculates the total amount paid across all linked payments.
     * Returns {@link BigDecimal#ZERO} if no payments exist yet.
     *
     * @return sum of all payment amounts for this rental
     */
    public BigDecimal totalPaid () {
        return payments.stream ()
                .map (Payment::getAmount)
                .reduce (BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns {@code true} if the disc has been returned (i.e. returnDate is
     * set), {@code false} if the rental is still open.
     *
     * @return whether this rental has been closed
     */
    public boolean isReturned () {
        return returnDate != null;
    }

}

