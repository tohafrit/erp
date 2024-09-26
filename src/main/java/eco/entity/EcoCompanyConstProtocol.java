package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Сущность с описанием таблицы ECOPLAN.COMPANY_CONST_PROTOCOL
 * @author mazur_ea
 * Date:   28.11.2019
 */
@Entity
@Table(name = "COMPANY_CONST_PROTOCOL")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoCompanyConstProtocol implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // идентификатор

    @Column(name = "protocol_number")
    private String protocolNumber; // номер протокола

    @Column(name = "protocol_date")
    private LocalDate protocolDate; // дата протокола

    @Column(name = "protocol_note")
    private String protocolNote; // комментарий

    @Column(name = "document_id")
    private Long documentId; // документ

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private EcoCompany company; // компания

    @Column(name = "additional_wages_rate")
    private double additionalWagesRate; // дополнительная заработная плата

    @Column(name = "social_insurance_rate")
    private double socialInsuranceRate; // отчисления на соц. страхование

    @Column(name = "profit_rate")
    private double profitRate; // прибыль - % от собственных затрат

    @Column(name = "manufacturing_charges_rate")
    private double manufacturingChargesRate; // общепроизводственные расходы

    @Column(name = "workshop_charges_rate")
    private double workshopChargesRate; // общехозяйственные расходы

    @Column(name = "average_monthly_pay")
    private double averageMonthlyPay; // средняя месячная з/п

    /**
     * Метод получает накладные расходы - сумма общепроизводственных и общехозяйственных расходов
     * @return накладные расходы
     */
    public double getOverhead() {
        return BigDecimal.valueOf(manufacturingChargesRate).add(BigDecimal.valueOf(workshopChargesRate)).doubleValue();
    }
}