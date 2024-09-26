package eco.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.korundm.enumeration.DocumentParentType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы DOCUMENT
 * @author zhestkov_an
 * Date:   29.08.2019
 */
@Entity
@Table(name = "DOCUMENT")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
public class EcoDocument implements Serializable {

    @Id
    @Column(name = "id")
    private Long id; // id

    @Convert(converter = DocumentParentType.CustomConverter.class)
    @Column(name = "parent_type")
    private DocumentParentType parentType; // тип владельца (enum DocumentParentType.kt)

    @Column(name = "parent_id")
    private Long parentId; // идентификатор владельца

    @Column(name = "doc_name")
    private String docName; // название документа

    @Column(name = "doc_note")
    private String docNote; // комментарий к документу

    @Column(name = "file_name")
    private String fileName; // имя файла

    @Column(name = "file_type")
    private Long fileType; // тип файла

    @Column(name = "order_index")
    private Long orderIndex; //

    @Column(name = "modify_date")
    private LocalDateTime modifyDate; //

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modify_user")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private EcoUserInfo modifyUser;

    @OneToOne(mappedBy = "document")
    @JsonIgnore
    private EcoPresentLogRecord presentLogRecord; // предъявление

    @OneToMany(mappedBy = "document")
    @JsonIgnore
    private List<EcoInOutDocument> inOutDocumentList = new ArrayList<>(); // список приходных документов
}