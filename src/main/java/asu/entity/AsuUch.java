package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.util.CommonUtil;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы tab_uchastp
 * @author pakhunov_an
 * Date:   25.11.2019
 */
@Entity
@Table(name = "tab_uchastp")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuUch implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "cod_uch")
    private String code;

    @Column(name = "naz_uch")
    private String name;

    @Transient
    private String fcode;

    public String getFcode(){
        return CommonUtil.formatZero(this.code, 2);
    }
}