package com.me.learning.parent.inventoryservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table (name = "store")
public class Store implements Serializable {

    @Serial
    private static final long serialVersionUID = -5353348518944263867L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "store_id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @OneToMany
    @JoinColumn (name = "store_id")
    private Set<Inventory> inventories = new LinkedHashSet<> ();

}
