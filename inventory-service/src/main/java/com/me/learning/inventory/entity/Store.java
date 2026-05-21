package com.me.learning.inventory.entity;

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


/**
 * Represents a physical store / rental location.
 *
 * <p><b>Cross-service references (stored as plain IDs):</b>
 * <ul>
 *   <li>{@code managerStaffId} — references {@code staff.staff_id} owned by
 *       <em>customer-service</em>.  No JPA join is created; the ID is
 *       resolved at runtime via a service-to-service call.</li>
 *   <li>{@code addressId} — references {@code address.address_id} owned by
 *       <em>customer-service</em>.  Same pattern.</li>
 * </ul>
 *
 * Maps to the {@code store} table in the Sakila schema.
 */
@Getter
@Setter
@Entity
@Table (name = "store")
public class Store {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "store_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    /**
     * ID of the staff member who manages this store.
     * Cross-service reference — {@code staff} is owned by <em>customer-service</em>.
     */
    @NotNull
    @Column (name = "manager_staff_id", nullable = false,
            columnDefinition = "tinyint UNSIGNED not null")
    private Short managerStaffId;

    /**
     * ID of the store's physical address.
     * Cross-service reference — {@code address} is owned by <em>customer-service</em>.
     */
    @NotNull
    @Column (name = "address_id", nullable = false,
            columnDefinition = "smallint UNSIGNED not null")
    private Integer addressId;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    /** All inventory items held at this store. */
    @OneToMany (mappedBy = "store")
    private Set<Inventory> inventories = new LinkedHashSet<> ();

}

