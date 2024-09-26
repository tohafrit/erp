package asu.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Сущность с описанием таблицы invoicestrings
 * @author mazur_ea
 * Date:   08.10.2019
 */
@Entity
@Table(name = "OKEIS")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class AsuOkei implements Serializable {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "coeff")
    private Double coeff;

    @Column(name = "name")
    private String name;

    @Column(name = "symbolNat")
    private String symbolNat;

    @Column(name = "symbolIntNat")
    private String symbolIntNat;

    @Column(name = "codeAlphNat")
    private String codeAlphNat;

    @Column(name = "codeAlphIntNat")
    private String codeAlphIntNat;
}