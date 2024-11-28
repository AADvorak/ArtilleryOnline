package com.github.aadvorak.artilleryonline.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PageResponse<T> {

    private List<T> items;

    private long itemsLength;

    private long pages;
}
