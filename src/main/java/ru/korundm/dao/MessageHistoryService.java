package ru.korundm.dao;

import ru.korundm.entity.MessageHistory;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author zhestkov_an
 * Date:   18.12.2018
 */
public interface MessageHistoryService extends CommonService<MessageHistory> {

    List<MessageHistory> searchByParams(Long type, Long user, LocalDate dateDeparture);
}