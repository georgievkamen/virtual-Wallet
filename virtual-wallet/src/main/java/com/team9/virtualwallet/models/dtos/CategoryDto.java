package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CategoryDto {

    @NotNull(message = "You must provide a category name!")
    @Size(min = 2, max = 20, message = "Category name must be between 2 and 20 characters long!")
    private String name;

    public CategoryDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
