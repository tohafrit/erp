package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static ru.korundm.constant.BaseConstant.GENERATOR_STRATEGY;

/**
 * Сущность с описанием таблицы bom_attributes
 * @author zhestkov_an
 * Date:   12.04.2020
 */
@Entity
@Table(name = "bom_attributes")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class BomAttribute {

    @Id
    @GeneratedValue(strategy = IDENTITY, generator = "bomAttribute")
    @GenericGenerator(name = "bomAttribute", strategy = GENERATOR_STRATEGY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "launch_id", nullable = false)
    private Launch launch; // запуск

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bom_id", nullable = false)
    private Bom bom; // версия изделия

    @Column(name = "approve_date")
    private LocalDate approveDate; // дата утвреждения

    @Column(name = "accept_date")
    private LocalDate acceptDate; // дата принятия

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "document_id")
    private Document document; // документ

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "struct_producer_id")
    private Company structProducer; // утвержденный изготовитель состава

    @Column(name = "struct_producer_date")
    private LocalDate structProducerDate; // дата утверждения изготовителя состава

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "struct_producer_user_id")
    private User structProducerUser; // пользователь утвердивший изготовителя для состава

    public LocalDate getApproveDate() {
        return approveDate;
    }
}