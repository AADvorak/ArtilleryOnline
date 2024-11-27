package com.github.aadvorak.artilleryonline.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SortDto {

    private String by;

    private String dir;
}
