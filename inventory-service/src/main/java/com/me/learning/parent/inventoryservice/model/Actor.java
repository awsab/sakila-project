package com.me.learning.parent.inventoryservice.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table (name = "actor")
public class Actor implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "actor_id", columnDefinition = "smallint UNSIGNED not null")
    @JdbcTypeCode (SqlTypes.SMALLINT)
    private Integer id;

    @Size (max = 45)
    @NotNull
    @Column (name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Size (max = 45)
    @NotNull
    @Column (name = "last_name", nullable = false, length = 45)
    private String lastName;

    @NotNull
    @ColumnDefault ("CURRENT_TIMESTAMP")
    @Column (name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    @OneToMany (mappedBy = "actor")
    private Set<FilmActor> actorFilmSet = new HashSet<> ();

    @PrePersist
    @PreUpdate
    protected void onCreateOrUpdate () {
        this.lastUpdate = LocalDateTime.now ();
    }
}
