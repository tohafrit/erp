package ru.korundm.form.search;

import java.io.Serializable;

/**
 * Форма поиска данных по вхождению для изделия
 * @author mazur_ea
 * Date:   05.12.2019
 */
public final class ProductOccurrenceFilterForm implements Serializable {

    private boolean active; // выпускаемые
    private boolean archive; // устаревшие
    private boolean lastApprove; // последний подтвержденный
    private boolean lastAccept; // последний принятый
    private boolean lastNumber; // последний номер
    private String approveSearchText; // текст поиска по подтвержденным
    private String acceptSearchText; // текст поиска по принятым

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public boolean isLastApprove() {
        return lastApprove;
    }

    public void setLastApprove(boolean lastApprove) {
        this.lastApprove = lastApprove;
    }

    public boolean isLastAccept() {
        return lastAccept;
    }

    public void setLastAccept(boolean lastAccept) {
        this.lastAccept = lastAccept;
    }

    public boolean isLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(boolean lastNumber) {
        this.lastNumber = lastNumber;
    }

    public String getApproveSearchText() {
        return approveSearchText;
    }

    public void setApproveSearchText(String approveSearchText) {
        this.approveSearchText = approveSearchText;
    }

    public String getAcceptSearchText() {
        return acceptSearchText;
    }

    public void setAcceptSearchText(String acceptSearchText) {
        this.acceptSearchText = acceptSearchText;
    }
}