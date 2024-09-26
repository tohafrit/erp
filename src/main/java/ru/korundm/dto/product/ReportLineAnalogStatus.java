package ru.korundm.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class ReportLineAnalogStatus {

    private String cell;
    private String name;
    private String description;
    private String purCell;
    private String purName;
    private String purDescription;
    private String productName;
    private String version;
    private String approved;
    private String accepted;
    private String developer;
    private String status;
}