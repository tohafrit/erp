package ru.korundm.dto.product;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class ReportLineSpecLaunch {

    private long productId;
    private String productName;
    private Long bomId;
    private String launchVersion;
    private String launchNumber;
    private String lastName;
    private String firstApproved;
    private String modified;
}