package com.github.aadvorak.artilleryonline.dto.request;

import com.github.aadvorak.artilleryonline.dto.SortDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest<FiltersRequest> {

    @PositiveOrZero
    private int page;

    @Min(5)
    @Max(25)
    private int size;

    private SortDto sort;

    private FiltersRequest filters;
}
