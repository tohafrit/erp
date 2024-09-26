package ru.korundm.report.word.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import ru.korundm.constant.BaseConstant;
import ru.korundm.enumeration.DocTemplateTagKey;
import ru.korundm.report.word.helper.CTBorderProperties;
import ru.korundm.report.word.helper.CTFontsProperties;
import ru.korundm.report.word.helper.CTIndProperties;
import ru.korundm.report.word.helper.CTSpacingProperties;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилити класс для работы с word-отчетами
 * <br>
 * Структуры в xml отображении:
 * <br>
 * /word/document.xml - структура документа
 * <br>
 * /word/settings.xml - структура настроек документа
 * <br>
 * /word/styles.xml - структура стилей документа
 * http://officeopenxml.com/anatomyofOOXML.php
 * @author pakhunov_an
 * Date:   06.11.2019
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordUtil {

    /**
     * Преобразование сантиметров в Twentieths of a point (dxa)
     * <br>
     * Стандартная мера почти для всех значений узлов
     * @param value значение в сантиметрах
     * @return значение в dxa
     */
    public static long cmToDXA(double value) {
        return (long) (value*72*20/2.54);
    }

    /**
     * Преобразование значения в pt к half-points
     * <br>
     * Используется в размерностях шрифтов
     * @param value значение в pt
     * @return значение в half-points
     */
    public static long ptToHalfPoints(long value) {
        return value*2;
    }

    /**
     * Преобразование значения в процентах к PCT
     * <br>
     * Используется для процентных отображений
     * @param value значение в процентах
     * @return значение в PCT
     */
    public static long percentToPCT(double value) {
        return Math.round(value*50);
    }

    /**
     * Преобразование значения в pt к unit
     * <br>
     * Используется в границах
     * @param value значение в pt
     * @return значение в unit
     */
    public static long ptToUnit(double value) {
        return Math.round(value*8);
    }

    /**
     * Стандартная установка свойства-высоты
     * @param ctHeight свойство-высота {@link CTHeight}
     * @param rule режим высоты {@link STHeightRule.Enum}
     * @param value значение в dxa
     */
    public static void defaultSetCTHeight(@NonNull CTHeight ctHeight, @NonNull STHeightRule.Enum rule, Long value) {
        ctHeight.setHRule(rule);
        if (STHeightRule.AUTO.equals(rule)) {
            ctHeight.setVal(BigInteger.ZERO);
        } else if (value == null) {
            ctHeight.setNil();
        } else {
            ctHeight.setVal(BigInteger.valueOf(value));
        }
    }

    /**
     * Стандартная установка свойства-строки
     * @param ctString свойство-строка {@link CTString}
     * @param value значение
     */
    public static void defaultSetCTString(@NonNull CTString ctString, String value) {
        if (value == null) {
            ctString.setNil();
        } else {
            ctString.setVal(value);
        }
    }

    /**
     * Стандартная установка свойства-размерности Hps
     * @param ctHpsMeasure свойство-рамерности Hps {@link CTHpsMeasure}
     * @param value значение в pt
     */
    public static void defaultSetCTHpsMeasure(@NonNull CTHpsMeasure ctHpsMeasure, Long value) {
        if (value == null) {
            ctHpsMeasure.setNil();
        } else {
            ctHpsMeasure.setVal(BigInteger.valueOf(value));
        }
    }

    /**
     * Стандартная установка знакового свойства размерности Hps
     * @param ctSignedHpsMeasure знаковое свойство размерности Hps {@link CTSignedHpsMeasure}
     * @param value значение
     */
    public static void defaultSetCTSignedHpsMeasure(@NonNull CTSignedHpsMeasure ctSignedHpsMeasure, Long value) {
        if (value == null) {
            ctSignedHpsMeasure.setNil();
        } else {
            ctSignedHpsMeasure.setVal(BigInteger.valueOf(value));
        }
    }

    /**
     * Стандартная установка свойства-переключателя
     * @param ctOnOff свойство-переключатель {@link CTOnOff}
     * @param onTrue значение при turn = true {@link STOnOff.Enum}
     * @param onFalse значение при turn = false {@link STOnOff.Enum}
     * @param turn true - вкл., false - выкл.
     */
    public static void defaultSetCTOnOff(@NonNull CTOnOff ctOnOff, @NonNull STOnOff.Enum onTrue, @NonNull STOnOff.Enum onFalse, Boolean turn) {
        if (turn == null) {
            ctOnOff.setNil();
        } else {
            ctOnOff.setVal(turn ? onTrue : onFalse);
        }
    }

    /**
     * Стандартная установка свойства-переключателя в ON/OFF
     * @param ctOnOff свойство-переключатель {@link CTOnOff}
     * @param turn true - вкл., false - выкл.
     */
    public static void defaultOnOffSetCTOnOff(@NonNull CTOnOff ctOnOff, Boolean turn) {
        defaultSetCTOnOff(ctOnOff, STOnOff.ON, STOnOff.OFF, turn);
    }

    /**
     * Стандартная установка свойства-числа
     * @param ctDecimalNumber свойство-число {@link CTDecimalNumber}
     * @param value значение
     */
    public static void defaultSetCTDecimalNumber(@NonNull CTDecimalNumber ctDecimalNumber, Long value) {
        if (value == null) {
            ctDecimalNumber.setNil();
        } else {
            ctDecimalNumber.setVal(BigInteger.valueOf(value));
        }
    }

    /**
     * Стандартная установка свойства вертикального слияния
     * @param ctvMerge свойство вертикального слияния {@link CTVMerge}
     * @param value значение {@link STMerge.Enum}
     */
    public static void defaultSetCTVMerge(@NonNull CTVMerge ctvMerge, STMerge.Enum value) {
        if (value == null) {
            ctvMerge.setNil();
        } else {
            ctvMerge.setVal(value);
        }
    }

    /**
     * Стандартная установка свойства выравнивания
     * @param ctJc свойство выравнивания {@link CTJc}
     * @param value значение {@link STJc.Enum}
     */
    public static void defaultSetCTJc(@NonNull CTJc ctJc, STJc.Enum value) {
        if (value == null) {
            ctJc.setNil();
        } else {
            ctJc.setVal(value);
        }
    }

    /**
     * Стандартная установка свойства границы
     * @param ctBorder свойство границы {@link CTBorder}
     * @param properties настройки {@link CTBorderProperties}
     */
    public static void defaultSetCTBorder(@NonNull CTBorder ctBorder, CTBorderProperties properties) {
        if (properties == null) {
            ctBorder.setNil();
        } else {
            if (properties.val() != null) {
                ctBorder.setVal(properties.val());
            } else {
                ctBorder.setNil();
            }
            if (properties.sz() != null) {
                ctBorder.setSz(BigInteger.valueOf(properties.sz()));
            } else if (ctBorder.isSetSz()) {
                ctBorder.unsetSz();
            }
            if (properties.space() != null) {
                ctBorder.setSpace(BigInteger.valueOf(properties.space()));
            } else if (ctBorder.isSetSpace()) {
                ctBorder.unsetSpace();
            }
            if (properties.color() != null) {
                ctBorder.setColor(properties.color());
            } else if (ctBorder.isSetColor()) {
                ctBorder.unsetColor();
            }
            if (properties.frame() != null) {
                ctBorder.setFrame(properties.frame());
            } else if (ctBorder.isSetFrame()) {
                ctBorder.unsetFrame();
            }
            if (properties.themeColor() != null) {
                ctBorder.setThemeColor(properties.themeColor());
            } else if (ctBorder.isSetThemeColor()) {
                ctBorder.unsetThemeColor();
            }
            if (properties.shadow() != null) {
                ctBorder.setShadow(properties.shadow());
            } else if (ctBorder.isSetShadow()) {
                ctBorder.unsetShadow();
            }
        }
    }

    /**
     * Стандартная установка свойства интервалов
     * @param ctSpacing свойство интервалов {@link CTSpacing}
     * @param properties настройки {@link CTSpacingProperties}
     */
    public static void defaultSetCTSpacing(@NonNull CTSpacing ctSpacing, CTSpacingProperties properties) {
        if (properties == null) {
            ctSpacing.setNil();
        } else {
            if (properties.line() != null) {
                ctSpacing.setLine(BigInteger.valueOf(properties.line()));
            } else if (ctSpacing.isSetLine()) {
                ctSpacing.unsetLine();
            }
            if (properties.lineRule() != null) {
                ctSpacing.setLineRule(properties.lineRule());
            } else if (ctSpacing.isSetLineRule()) {
                ctSpacing.unsetLineRule();
            }
            if (properties.after() != null) {
                ctSpacing.setAfter(BigInteger.valueOf(properties.after()));
            } else if (ctSpacing.isSetAfter()) {
                ctSpacing.unsetAfter();
            }
            if (properties.afterAutoSpacing() != null) {
                ctSpacing.setAfterAutospacing(properties.afterAutoSpacing());
            } else if (ctSpacing.isSetAfterAutospacing()) {
                ctSpacing.unsetAfterAutospacing();
            }
            if (properties.afterLines() != null) {
                ctSpacing.setAfterLines(BigInteger.valueOf(properties.afterLines()));
            } else if (ctSpacing.isSetAfterLines()) {
                ctSpacing.unsetAfterLines();
            }
            if (properties.before() != null) {
                ctSpacing.setBefore(BigInteger.valueOf(properties.before()));
            } else if (ctSpacing.isSetBefore()) {
                ctSpacing.unsetBefore();
            }
            if (properties.beforeAutoSpacing() != null) {
                ctSpacing.setBeforeAutospacing(properties.beforeAutoSpacing());
            } else if (ctSpacing.isSetBeforeAutospacing()) {
                ctSpacing.unsetBeforeAutospacing();
            }
            if (properties.beforeLines() != null) {
                ctSpacing.setBeforeLines(BigInteger.valueOf(properties.beforeLines()));
            } else if (ctSpacing.isSetBeforeLines()) {
                ctSpacing.unsetBeforeLines();
            }
        }
    }

    /**
     * Стандартная установка свойства отступов
     * @param ctInd свойство отступов {@link CTInd}
     * @param properties настройки {@link CTIndProperties}
     */
    public static void defaultSetCTInd(CTInd ctInd, CTIndProperties properties) {
        if (properties == null) {
            ctInd.setNil();
        } else {
            if (properties.left() != null) {
                ctInd.setLeft(BigInteger.valueOf(properties.left()));
            } else if (ctInd.isSetLeft()) {
                ctInd.unsetLeft();
            }
            if (properties.right() != null) {
                ctInd.setRight(BigInteger.valueOf(properties.right()));
            } else if (ctInd.isSetRight()) {
                ctInd.unsetRight();
            }
            if (properties.firstLine() != null) {
                ctInd.setFirstLine(BigInteger.valueOf(properties.firstLine()));
            } else if (ctInd.isSetFirstLine()) {
                ctInd.unsetFirstLine();
            }
            if (properties.hanging() != null) {
                ctInd.setHanging(BigInteger.valueOf(properties.hanging()));
            } else if (ctInd.isSetHanging()) {
                ctInd.unsetHanging();
            }
        }
    }

    /**
     * Стандартная установка свойства шрифта
     * @param ctFonts свойство шрифта {@link CTFonts}
     * @param properties настройки {@link CTFontsProperties}
     */
    public static void defaultSetCTFonts(CTFonts ctFonts, CTFontsProperties properties) {
        if (properties == null) {
            ctFonts.setNil();
        } else {
            if (properties.cs() != null) {
                ctFonts.setCs(properties.cs());
            } else if (ctFonts.isSetCs()) {
                ctFonts.unsetCs();
            }
            if (properties.csTheme() != null) {
                ctFonts.setCstheme(properties.csTheme());
            } else if (ctFonts.isSetCstheme()) {
                ctFonts.unsetCstheme();
            }
            if (properties.ascii() != null) {
                ctFonts.setAscii(properties.ascii());
            } else if (ctFonts.isSetAscii()) {
                ctFonts.unsetAscii();
            }
            if (properties.asciiTheme() != null) {
                ctFonts.setAsciiTheme(properties.asciiTheme());
            } else if (ctFonts.isSetAsciiTheme()) {
                ctFonts.unsetAsciiTheme();
            }
            if (properties.eastAsia() != null) {
                ctFonts.setEastAsia(properties.eastAsia());
            } else if (ctFonts.isSetEastAsia()) {
                ctFonts.unsetEastAsia();
            }
            if (properties.eastAsiaTheme() != null) {
                ctFonts.setEastAsiaTheme(properties.eastAsiaTheme());
            } else if (ctFonts.isSetEastAsiaTheme()) {
                ctFonts.unsetEastAsiaTheme();
            }
            if (properties.hAnsi() != null) {
                ctFonts.setHAnsi(properties.hAnsi());
            } else if (ctFonts.isSetHAnsi()) {
                ctFonts.unsetHAnsi();
            }
            if (properties.hAnsiTheme() != null) {
                ctFonts.setHAnsiTheme(properties.hAnsiTheme());
            } else if (ctFonts.isSetHAnsiTheme()) {
                ctFonts.unsetHAnsiTheme();
            }
            if (properties.hint() != null) {
                ctFonts.setHint(properties.hint());
            } else if (ctFonts.isSetHint()) {
                ctFonts.unsetHint();
            }
        }
    }

    /**
     * Замена ключей в параграфе
     * @param paragraph параграф данных
     * @param data      данные для замены в формате ключ-значение.
     */
    public static void replaceTagInText(XWPFParagraph paragraph, Map<String, String> data) {
        if (paragraph == null) {
            return;
        }
        String dateTagKey = DocTemplateTagKey.CURRENT_DATE.name();
        String dateTagValue = LocalDate.now().format(DateTimeFormatter.ofPattern(BaseConstant.DATE_PATTERN));
        data.put(dateTagKey, dateTagValue);
        String paragraphText = paragraph.getText();
        if (paragraphText.contains("${")) {
            TreeMap<Integer, XWPFRun> posRuns = getPosToRuns(paragraph);
            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher matcher = pattern.matcher(paragraphText);
            while (matcher.find()) {
                String group = matcher.group(1);
                int start = matcher.start(1);
                int end = matcher.end(1);
                String x = data.get(group);
                if (x == null)
                    x = "";
                SortedMap<Integer, XWPFRun> range = posRuns.subMap(start - 2, true, end + 1, true);
                boolean found1 = false;
                boolean found2 = false;
                boolean found3 = false;
                XWPFRun prevRun = null;
                XWPFRun found2Run = null;
                int found2Pos = -1;
                for (var xwpfRun : range.values()) {
                    if (xwpfRun != prevRun) {
                        if (found3)
                            break;
                        prevRun = xwpfRun;
                        for (int k = 0; ; k++) {
                            if (found3) {
                                break;
                            }
                            String txt = null;
                            try {
                                txt = xwpfRun.getText(k);
                            } catch (Exception ex) {
                                //do nothing
                            }
                            if (txt == null) {
                                break;
                            }
                            if (txt.contains("$") && !found1) {
                                txt = txt.replaceFirst("\\$", x);
                                found1 = true;
                            }
                            if (txt.contains("{") && !found2 && found1) {
                                found2Run = xwpfRun;
                                found2Pos = txt.indexOf('{');
                                txt = txt.replaceFirst("\\{", "");
                                found2 = true;
                            }
                            if (found1 && found2 && !found3) {
                                if (txt.contains("}")) {
                                    if (xwpfRun == found2Run) {
                                        txt = txt.substring(0, found2Pos) + txt.substring(txt.indexOf('}'));
                                    } else
                                        txt = txt.substring(txt.indexOf('}'));
                                } else if (xwpfRun == found2Run) {
                                    txt = txt.substring(0, found2Pos);
                                } else{
                                    txt = "";
                                }
                            }
                            if (txt.contains("}") && !found3) {
                                txt = txt.replaceFirst("\\}", "");
                                found3 = true;
                            }
                            xwpfRun.setText(txt, k);
                        }
                    }
                }
            }
        }
    }

    /**
     * Получение дерева позиций run в параграфах
     * @param paragraph параграф данных
     * @return дерево позиций
     */
    private static TreeMap<Integer, XWPFRun> getPosToRuns(XWPFParagraph paragraph) {
        int pos = 0;
        Map<Integer, XWPFRun> map = new TreeMap<>();
        for (var run : paragraph.getRuns()) {
            String runText = run.text();
            if (runText != null && runText.length() > 0) {
                for (int i = 0; i < runText.length(); i++) {
                    map.put(pos + i, run);
                }
                pos += runText.length();
            }
        }
        return (TreeMap<Integer, XWPFRun>) map;
    }

    /**
     * Заменить метку на строках таблиц согласно параметрам Карта
     * @param tableList список таблиц
     * @param data параметры карты
     */
    public static void replaceTableRow(List<XWPFTable> tableList, Map<String, String> data) {
        for (var table: tableList) {
            for (var tableRow: table.getRows()) {
                List<XWPFTableCell> tableCells = tableRow.getTableCells();
                for (XWPFTableCell xWPFTableCell : tableCells) {
                    List<XWPFParagraph> paragraphs = xWPFTableCell.getParagraphs();
                    for (XWPFParagraph xwpfParagraph : paragraphs) {
                        replaceTagInText(xwpfParagraph, data);
                    }
                }
            }
        }
    }
}