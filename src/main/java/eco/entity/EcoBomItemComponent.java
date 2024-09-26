package eco.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Сущность с описанием таблицы BOM_ITEM_COMPONENT
 * @author mazur_ea
 * Date:   07.08.2019
 */
@Entity
@Table(name = "BOM_ITEM_COMPONENT")
@ToString
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class EcoBomItemComponent implements Serializable {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bom_item_id")
    private EcoBomItem bomItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id")
    private EcoBomComponent component;

    @Column(name = "kd")
    private boolean kd;

    @Column(name = "purchase")
    private boolean purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status")
    private EcoTBicStatus status;

    @Column(name = "date_replaced")
    private LocalDate dateReplaced;

    @Column(name = "date_processed")
    private LocalDate dateProcessed;
}