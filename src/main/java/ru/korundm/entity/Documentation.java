package ru.korundm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность с описанием таблицы documentation
 * @author pakhunov_an
 * Date:   22.04.2019
 */
@Entity
@Table(name = "documentation")
@ToString
@Setter @Getter
@EqualsAndHashCode(of = "id")
@Indexed
@AnalyzerDef(
    name = "customAnalyzer",
    charFilters = {@CharFilterDef(factory = HTMLStripCharFilterFactory.class)},
    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
    filters = {
        @TokenFilterDef(factory = LowerCaseFilterFactory.class),
        @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
            @Parameter(name = "language", value = "Russian")
        })
    }
)
public class Documentation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id; // id

    @Column(name = "name", nullable = false)
    @Field
    @Analyzer(definition = "customAnalyzer")
    private String name; // название

    @Column(name = "content")
    @Field(termVector = TermVector.WITH_POSITIONS)
    @Analyzer(definition = "customAnalyzer")
    private String content; // текст

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Documentation parent; // родительская документация

    @OneToMany(mappedBy = "parent", cascade  = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name asc")
    @JsonIgnore
    private List<Documentation> childList = new ArrayList<>(); // список документаций

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", referencedColumnName = "id")
    @JsonIgnore
    private MenuItem menuItem; // пункт меню

    @ManyToMany
    @JoinTable(
        name = "documentation_seealso",
        joinColumns = @JoinColumn(name = "first"),
        inverseJoinColumns = @JoinColumn(name = "second")
    )
    private List<Documentation> seeAlsoList = new ArrayList<>(); // список связанных страниц документации
}