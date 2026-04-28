package com.me.learning.parent.inventoryservice.model;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table (name = "film_text")
public class FilmText implements Serializable {

    @Serial
    private static final long serialVersionUID = -4144479260676525241L;

    @Id
    @Column (name = "film_id", nullable = false)
    private Short id;

    @Size (max = 255)
    @NotNull
    @Column (name = "title", nullable = false)
    private String title;

    @Lob
    @Column (name = "description")
    private String description;

}
