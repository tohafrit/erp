package ru.korundm.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class ReportLineMissAnalog {

    private String cell;
    private String name;
    private String description;
    private String purCell;
    private String purName;
    private String purDescription;
    private String productName;
    private String version;
    private String launches;
}