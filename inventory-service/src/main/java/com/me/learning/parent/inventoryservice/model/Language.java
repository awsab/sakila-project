/**
 * Author   : Prabakaran Ramu
 * User     : ramup
 * Date     : 11/03/2026
 * Usage    :
 * Since    : Version 1.0
 */
package com.me.learning.parent.inventoryservice.model;

import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Language implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "language_id", columnDefinition = "tinyint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.TINYINT)
    private Short id;

    @Size (max = 25)
    @NotNull
    @Column (name = "name", nullable = false, length = 25)
    private String name;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private Instant lastUpdate;

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate () {
        this.lastUpdate = Instant.now ();
    }
}
