package ru.korundm.form.edit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import ru.korundm.constant.ValidatorMsg;
import ru.korundm.helper.ValidatorErrors;
import ru.korundm.helper.Validatable;
import ru.korundm.util.CommonUtil;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Форма добавления позиции ко вхождению в спецификацию изделия
 * @author mazur_ea
 * Date:   15.07.2020
 */
@Getter
@Setter
@ToString
public class EditProductSpecItemForm implements Validatable {

    private static final String DESIGNATION_SEPARATOR = ",";
    private static final String DESIGNATION_DIAPASON_SEPARATOR = "-";

    private static final int LIMIT_LETTER_POSITION_VALUE = 500;
    private static final Pattern patternLetter = Pattern.compile("^[A-Z]{1,3}$");
    private static final Pattern patternLetterValue = Pattern.compile("^((([1-9][0-9]*)|([1-9][0-9]*[-][1-9][0-9]*))([,](([1-9][0-9]*)|([1-9][0-9]*[-][1-9][0-9]*)))*)$");

    @Getter @Setter
    @EqualsAndHashCode(of = "letter")
    public static class Position {
        private String letter; // литера
        private String value; // значение
    }

    private Long id;
    private boolean isUnit; // флаг штучной категории
    private long lockVersion; // версия блокировки
    private Double quantity; // количество
    private boolean givenRawMaterial; // давальческое сырье
    private List<Position> positionList = new ArrayList<>(); // список позиций штучных категорий
    private List<Long> producerIdList = new ArrayList<>(); // список идентификаторов изготовителей
    private boolean isApprovedOrAccepted;

    @Override
    public void validate(@NotNull ValidatorErrors errors) {
        if (givenRawMaterial && producerIdList.isEmpty()) {
            errors.putError("producerIdList", ValidatorMsg.REQUIRED);
        }
        if (isApprovedOrAccepted) { // Проверки на количество не нужны, спецификация утверждена
            return;
        }
        if (isUnit) {
            if (quantity != null && quantity % 1 != 0) {
                errors.putError("quantity", "validator.editProductSpecItemForm.quantity");
            }
            boolean hasBlank = positionList.stream().anyMatch(position -> StringUtils.isBlank(position.getLetter()) || StringUtils.isBlank(position.getValue()));
            if (hasBlank) {
                errors.putError("positionList", "validator.editProductSpecItemForm.notBlankPositionValues");
            } else {
                // Литеры
                boolean isLetterIncorrect = positionList.stream().anyMatch(position -> !patternLetter.matcher(position.getLetter()).matches());
                if (isLetterIncorrect) {
                    errors.putError("positionList", "validator.editProductSpecItemForm.incorrectLetter");
                }
                boolean isLetterDuplicate = positionList.stream().anyMatch(position -> Collections.frequency(positionList, position) > 1);
                if (isLetterDuplicate) {
                    errors.putError("positionList", "validator.editProductSpecItemForm.letterDuplicate");
                }
                // Значения
                boolean isLetterValIncorrect = positionList.stream().anyMatch(position -> !patternLetterValue.matcher(position.getValue()).matches());
                if (isLetterValIncorrect) {
                    errors.putError("positionList", "validator.editProductSpecItemForm.incorrectLetterValue");
                } else {
                    Map<String, List<Integer>> mapPositionNumberList = new HashMap<>();
                    posMark : for (var position : positionList) {
                        String letter = position.getLetter();
                        // Получение списка позиций для литеры
                        List<Integer> positionNumberList;
                        if (mapPositionNumberList.containsKey(letter)) {
                            positionNumberList = mapPositionNumberList.get(letter);
                        } else {
                            positionNumberList = new ArrayList<>();
                            mapPositionNumberList.put(letter, positionNumberList);
                        }
                        // Валидация диапазонов
                        String[] diapasons = StringUtils.defaultIfBlank(position.getValue(), "").split(DESIGNATION_SEPARATOR);
                        for (var diapason : diapasons) {
                            if (diapason.contains(DESIGNATION_DIAPASON_SEPARATOR)) {
                                String[] diapasonArr = diapason.split(DESIGNATION_DIAPASON_SEPARATOR);
                                Integer first = CommonUtil.convertStringToType(diapasonArr[0], Integer.class);
                                Integer second = CommonUtil.convertStringToType(diapasonArr[1], Integer.class);
                                // Если в диапазоне первое значение больше или равно второму (1-1,3-2)
                                if (second != null && first != null) {
                                    if (second - first < 1) {
                                        errors.putError("positionList", "validator.editProductSpecItemForm.letterValueDiapasonIncorrect");
                                        break posMark;
                                    }
                                    // Проверка на повторы
                                    if (positionNumberList.contains(first) || positionNumberList.contains(second)) {
                                        errors.putError("positionList", "validator.editProductSpecItemForm.letterValuePositionExists");
                                        break posMark;
                                    }
                                    for (int i = first; i <= second; i++) {
                                        positionNumberList.add(i);
                                    }
                                }
                            } else {
                                Integer pos = CommonUtil.convertStringToType(diapason, Integer.class);
                                // Проверка на повторы
                                if (positionNumberList.contains(pos)) {
                                    errors.putError("positionList", "validator.editProductSpecItemForm.letterValuePositionExists");
                                    break posMark;
                                }
                                positionNumberList.add(pos);
                            }
                        }
                    }
                    // Проверка на лимит значения позиции
                    for (var entry : mapPositionNumberList.entrySet()) {
                        if (entry.getValue().stream().anyMatch(position -> position > LIMIT_LETTER_POSITION_VALUE)) {
                            errors.putError("positionList", "validator.editProductSpecItemForm.letterValuePositionLimit", LIMIT_LETTER_POSITION_VALUE);
                        }
                    }
                }
            }
        } else {
            if (quantity == null) {
                errors.putError("quantity", ValidatorMsg.REQUIRED);
            }
        }
    }
}