package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "tab_oper")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuOperation implements Serializable {

    @Id
    @Column(name = "ROWID")
    private Long id;

    @Column(name = "cod_oper")
    private String code;

    @Column(name = "toPS")
    private boolean toPS;

    public String getFCode(){
        String nulString = "000000000000000000000000000";
        int l;

        if ((l = code.indexOf ('.')) == -1) {
            l = code.length ();
        }
        return 6 - l > 0 ? nulString.substring(0, 6-l) + code.substring(0, l) : code.substring(0, l);
    }
}