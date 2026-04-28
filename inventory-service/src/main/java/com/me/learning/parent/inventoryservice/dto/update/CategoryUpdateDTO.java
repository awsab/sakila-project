package com.me.learning.parent.inventoryservice.dto.update;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull (message = "Category ID is required for update")
    @JsonProperty ("categoryId")
    private Integer id;

    @NotBlank (message = "Category name is required")
    @Size (max = 25, message = "Category name must not exceed 25 characters")
    @JsonProperty ("name")
    private String name;
}
