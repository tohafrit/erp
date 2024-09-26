package ru.korundm.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.WaterType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы оборудования equipments
 * @author pakhunov_an
 * Date:   27.03.2018
 */
@Entity
@Table(name = "equipments")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class Equipment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // идентификатор

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producer_id")
    private Producer producer; // производитель

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType; // тип оборудования

    @Column(name = "name", nullable = false)
    private String name; // наименование

    @Column(name = "model")
    private String model; // модель

    @Column(name = "weight")
    private Integer weight; // масса

    @Column(name = "voltage")
    private String voltage; // напряжение

    @Column(name = "power")
    private Integer power; // мощность

    @Column(name = "dimensions")
    private String dimensions; // габариты

    @Column(name = "compressed_air_pressure")
    private String compressedAirPressure; // сжатый воздух - давление

    @Column(name = "compressed_air_consumption")
    private String compressedAirConsumption; // сжатый воздух - расход

    @Column(name = "nitrogen_pressure")
    private String nitrogenPressure; // азот

    @Column(name = "water")
    private String water; // вода (WaterType.java)

    @Column(name = "sewerage")
    private boolean sewerage; // канализация

    @Column(name = "extractor_volume")
    private String extractorVolume; // вытяжка - объем

    @Column(name = "extractor_diameter")
    private String extractorDiameter; // вытяжка - диаметр

    @Column(name = "link")
    private String link; // документация

    @OneToMany(mappedBy = "equipment", cascade = CascadeType.ALL)
    private List<EquipmentUnit> equipmentUnitList = new ArrayList<>(); // список единиц оборудования

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // сотрудник

    @Column(name = "shift")
    private Integer shift = 1; // сменность

    @Column(name = "archive", nullable = false)
    private boolean archive; // архивность

    /**
     * Метод для определния ключа локализации
     * @return ключ
     */
    public String getWaterType() {
        WaterType waterType = WaterType.getByType(water);
        return waterType != null ? waterType.getProperty() : "";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }
}