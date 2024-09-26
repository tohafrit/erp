package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилити класс для работы с документом в word-отчетах
 * <br>
 * В xml отображении - part pkg:name="/word/document.xml"
 * <br>
 * Имеет тег - <b>w:document</b>
 * @author mazur_ea
 * Date:   21.01.2020
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DocumentUtil {

    /**
     * Установка ориентации и размера бумаги для документа
     * <br>
     * Параметры страницы -> размеры бумаги
     * <br>
     * Параметры страницы -> ориентация
     * @param document      документ {@link XWPFDocument}
     * @param orientation   ориентация {@link STPageOrientation.Enum}
     * @param width         ширина в dxa
     * @param height        высота в dxa
     */
    public static void orientSize(
        @NonNull XWPFDocument document,
        @NonNull STPageOrientation.Enum orientation,
        long width,
        long height
    ) {
        CTBody ctBody = document.getDocument().getBody();
        CTSectPr ctSectPr = ctBody.isSetSectPr() ? ctBody.getSectPr() : ctBody.addNewSectPr();
        CTPageSz ctPageSz = ctSectPr.isSetPgSz() ? ctSectPr.getPgSz() : ctSectPr.addNewPgSz();
        ctPageSz.setOrient(orientation);
        ctPageSz.setW(BigInteger.valueOf(width));
        ctPageSz.setH(BigInteger.valueOf(height));
    }

    /**
     * Изменение ориентации и размера бумаги для документа
     * <br>
     * Параметры страницы -> размеры бумаги
     * <br>
     * Параметры страницы -> ориентация
     * @param document      документ {@link XWPFDocument}
     * @param orientation   ориентация {@link STPageOrientation.Enum}
     * @param width         ширина в dxa
     * @param height        высота в dxa
     * @param top           отступ верхнего поля в dxa
     * @param right         отступ правого поля в dxa
     * @param bottom        отступ нижнего поля в dxa
     * @param left          отступ левого поля в dxa
     */
    public static void changeOrientation(
        XWPFDocument document,
        @NonNull STPageOrientation.Enum orientation,
        long width,
        long height,
        long top,
        long right,
        long bottom,
        long left
    ) {
        CTBody body = document.getDocument().getBody();
        CTSectPr section = body.addNewSectPr();
        XWPFParagraph para = document.createParagraph();
        CTP ctp = para.getCTP();
        CTPPr br = ctp.addNewPPr();
        br.setSectPr(section);
        CTPageSz pageSize =  section.isSetPgSz() ? section.getPgSz() : section.addNewPgSz();
        pageSize.setOrient(orientation);
        pageSize.setW(BigInteger.valueOf(width));
        pageSize.setH(BigInteger.valueOf(height));

        List<CTSectPr> allSectPr = new ArrayList<>();
        for (var paragraph : document.getParagraphs()) {
            CTPPr ctpPr = paragraph.getCTP().getPPr();
            if (ctpPr != null && paragraph.getCTP().getPPr().getSectPr() != null) {
                allSectPr.add(paragraph.getCTP().getPPr().getSectPr());
            }
        }
        allSectPr.add(document.getDocument().getBody().getSectPr());
        for (var sectPr : allSectPr) {
            CTPageMar pageMar = sectPr.isSetPgMar() ? sectPr.getPgMar() : sectPr.addNewPgMar();
            pageMar.setLeft(BigInteger.valueOf(left));
            pageMar.setTop(BigInteger.valueOf(top));
            pageMar.setRight(BigInteger.valueOf(right));
            pageMar.setBottom(BigInteger.valueOf(bottom));
        }
    }

    /**
     * Установка отступов страниц документа
     * <br>
     * Параметры страницы -> Поля
     * <br>
     * Параметры страницы -> Источник бумаги -> Различать колонтитулы
     * @param document  документ {@link XWPFDocument}
     * @param top       отступ верхнего поля в dxa
     * @param right     отступ правого поля в dxa
     * @param bottom    отступ нижнего поля в dxa
     * @param left      отступ левого поля в dxa
     * @param header    до верхненго колонтитула в dxa
     * @param footer    до нижнего колонтитула в dxa
     * @param gutter    переплет в dxa
     */
    public static void pageMargin(
        @NonNull XWPFDocument document,
        Long top,
        Long right,
        Long bottom,
        Long left,
        Long header,
        Long footer,
        Long gutter
    ) {
        CTBody ctBody = document.getDocument().getBody();
        CTSectPr ctSectPr = ctBody.isSetSectPr() ? ctBody.getSectPr() : ctBody.addNewSectPr();
        CTPageMar ctPageMar = ctSectPr.isSetPgMar() ? ctSectPr.getPgMar() : ctSectPr.addNewPgMar();
        if (top != null) {
            ctPageMar.setTop(BigInteger.valueOf(top));
        }
        if (right != null) {
            ctPageMar.setRight(BigInteger.valueOf(right));
        }
        if (bottom != null) {
            ctPageMar.setBottom(BigInteger.valueOf(bottom));
        }
        if (left != null) {
            ctPageMar.setLeft(BigInteger.valueOf(left));
        }
        if (header != null) {
            ctPageMar.setHeader(BigInteger.valueOf(header));
        }
        if (footer != null) {
            ctPageMar.setFooter(BigInteger.valueOf(footer));
        }
        if (gutter != null) {
            ctPageMar.setGutter(BigInteger.valueOf(gutter));
        }
    }
}