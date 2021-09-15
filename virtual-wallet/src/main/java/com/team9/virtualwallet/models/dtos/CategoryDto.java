package com.team9.virtualwallet.models.dtos;

import javax.validation.constraints.Size;

public class CategoryDto {

    @Size(min = 2, max = 16)
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
