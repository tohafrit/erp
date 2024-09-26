package ru.korundm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Класс общего назначения
 * @author surov_pv
 * Date:   30.03.2018
 */
@JBossLog
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonUtil {

    private static final char[] cyrillicAlphabet = {' ','а','б','в','г','д','е','ё', 'ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х', 'ц','ч', 'ш','щ','ъ','ы','ь','э', 'ю','я','А','Б','В','Г','Д','Е','Ё', 'Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х', 'Ц', 'Ч','Ш', 'Щ','Ъ','Ы','Ь','Э','Ю','Я','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static final String[] latinAlphabet = {" ","a","b","v","g","d","e","e","zh","z","i","y","k","l","m","n","o","p","r","s","t","u","f","h","ts","ch","sh","sch", "","i", "","e","ju","ja","A","B","V","G","D","E","E","Zh","Z","I","Y","K","L","M","N","O","P","R","S","T","U","F","H","Ts","Ch","Sh","Sch", "","I", "","E","Ju","Ja","a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    /**
     * Добавление нулей в начало строки
     * @param string      входная строка
     * @param totalLength необходимая длина
     * @return String
     */
    public static String formatZero(String string, int totalLength) {
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < (totalLength - string.length()); i++){
            stringBuilder.insert(0, '0');
        }
        return stringBuilder.toString();
    }

    /**
     * Метод преобразования строки в заданый тип
     * @param str строка
     * @param type тип {@link Integer,Long,Float,Double}
     * @return null, если преобразование не удалось
     */
    public static <Type> Type convertStringToType(String str, Class<Type> type) {
        Object result = null;
        if (Objects.equals(type, Integer.class)) {
            if (isInteger(str)) result = Integer.valueOf(str);
        } else if (Objects.equals(type, Long.class)) {
            if (isLong(str)) result = Long.valueOf(str);
        } else if (Objects.equals(type, Float.class)) {
            if (isFloat(str)) result = Float.valueOf(str);
        } else if (Objects.equals(type, Double.class)) {
            if (isDouble(str)) result = Double.valueOf(str);
        }
        return result == null ? null : type.cast(result);
    }

    /**
     * Метод проверки строки на {@link Integer}
     * @param str строка
     * @return true - если строка является {@link Integer}, иначе false
     */
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Метод проверки строки на {@link Long}
     * @param str строка
     * @return true - если строка является {@link Long}, иначе false
     */
    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Метод проверки строки на {@link Float}
     * @param str строка
     * @return true - если строка является {@link Float}, иначе false
     */
    public static boolean isFloat(String str) {
        try {
            Float.parseFloat(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Метод проверки строки на {@link Double}
     * @param str строка
     * @return true - если строка является {@link Double}, иначе false
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Метод проверки строки на целое число
     * @param str строка
     * @return true - если строка является целым числом, иначе false
     */
    public static boolean isWholeNumber(String str) {
        return Pattern.compile("^[\\-]?[0-9]+").matcher(str).matches();
    }

    /**
     * Метод для проверки разницы между датами
     * @param dateFrom с даты
     * @param dateTo по дату
     * @return true - если dateFrom больше dateTo, иначе false
     */
    public static boolean dateFromMoreThenTo(LocalDate dateFrom, LocalDate dateTo) {
        return dateFrom != null && dateTo != null && DAYS.between(dateFrom, dateTo) < 0;
    }

    /**
     * Метод для проверки разницы между датовременами
     * @param dateTimeFrom с датовремени
     * @param dateTimeTo по датовремя
     * @return true - если dateTimeFrom больше dateTimeTo, иначе false
     */
    public static boolean dateTimeFromMoreThenTo(LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo) {
        return dateTimeFrom != null && dateTimeTo != null && SECONDS.between(dateTimeFrom, dateTimeTo) < 0;
    }

    /**
     * Метод для перевода ФИО сотрудника из кириллицы в латиницу
     * @param fio ФИО
     * @return транслитерат ФИО
     */
    public static String transliterate(String fio){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fio.length(); i++) {
            for (int x = 0; x < cyrillicAlphabet.length; x++) {
                if (fio.charAt(i) == cyrillicAlphabet[x]) {
                    builder.append(latinAlphabet[x]);
                }
            }
        }
        return builder.toString();
    }

    /**
     * Метод преобразования сантиметров в дюймы
     * @param cm сантиметры
     * @return дюймы
     */
    public static double cmToInch(double cm) {
        return cm / 2.54;
    }


    private static final String[][] dig1 = {
        {"одна", "две", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять"},
        {"один", "два"}
    };
    private static final String[] dig10 = {"десять", "одиннадцать", "двенадцать", "тринадцать", "четырнадцать", "пятнадцать", "шестнадцать", "семнадцать", "восемнадцать", "девятнадцать"};
    private static final String[] dig20 = {"двадцать", "тридцать", "сорок", "пятьдесят", "шестьдесят", "семьдесят", "восемьдесят", "девяносто"};
    private static final String[] dig100 = {"сто", "двести", "триста", "четыреста", "пятьсот", "шестьсот", "семьсот", "восемьсот", "девятьсот"};
    private static final String[][] leword = {
        {"коп.", "коп.", "коп.", "0"},
        {"руб.", "руб.", "руб.", "1"},
        {"тысяча", "тысячи", "тысяч", "0"},
        {"миллион", "миллиона", "миллионов", "1"},
        {"миллиард", "миллиарда", "миллиардов", "1"},
        {"триллион", "триллиона", "триллионов", "1"}
    };


    //рекурсивная функция преобразования целого числа num в рубли
    private static String num2words(long num, int level) {
        StringBuilder words = new StringBuilder(50);
        if (num == 0) words.append("ноль ");         //исключительный случай
        int sex = leword[level][3].indexOf("1") + 1;
        int h = (int) (num % 1000);    //текущий трехзначный сегмент
        int d = h / 100;              //цифра сотен
        if (d > 0) words.append(dig100[d - 1]).append(" ");
        int n = h % 100;
        d = n / 10;                   //цифра десятков
        n = n % 10;                   //цифра единиц
        switch (d) {
            case 0:
                break;
            case 1:
                words.append(dig10[n]).append(" ");
                break;
            default:
                words.append(dig20[d - 2]).append(" ");
        }
        if (d == 1) n = 0;              //при двузначном остатке от 10 до 19, цифра едициц не должна учитываться
        switch (n) {
            case 0:
                break;
            case 1:
            case 2:
                words.append(dig1[sex][n - 1]).append(" ");
                break;
            default:
                words.append(dig1[0][n - 1]).append(" ");
        }
        switch (n) {
            case 1:
                words.append(leword[level][0]);
                break;
            case 2:
            case 3:
            case 4:
                words.append(leword[level][1]);
                break;
            default:
                if (h != 0 && level != 1)  //если трехзначный сегмент = 0, то добавлять нужно только "рублей"
                    words.append(leword[level][2]);
        }
        long nextNum = num / 1000;
        if (nextNum > 0) {
            return (num2words(nextNum, level + 1) + " " + words).trim();
        } else {
            return words.toString().trim();
        }
    }

    /**
     * Метод для вывода суммы прописью
     * при значении money более 50-70 триллионов рублей начинает искажать копейки, осторожней при работе такими суммами
     * @param money    сумма
     * @return сумма прописью
     */
    public static String moneyToWords(BigDecimal money) {
        String sm = String.format("%.2f", money);
        String skop = sm.substring(sm.length() - 2);    //значение копеек в строке

        long num = (long) Math.floor(money.doubleValue());
        if (num < 1000000000000000L) {
            return "(" + StringUtils.capitalize(num2words(num, 1)) + ") " + leword[1][0] + " " + skop + " " + leword[0][0];
        } else
            return "error: слишком много рублей " + skop + " " + leword[0][0];
    }
}