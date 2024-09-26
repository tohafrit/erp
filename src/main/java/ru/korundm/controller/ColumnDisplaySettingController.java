package ru.korundm.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.korundm.dao.ColumnDisplaySettingService;
import ru.korundm.entity.ColumnDisplaySetting;
import ru.korundm.entity.User;
import ru.korundm.util.KtCommonUtil;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Контроллер для работы с пользовательскими настройками отображения колонок {@link ColumnDisplaySetting}
 * @author pakhunov_an
 * Date:   15.05.2018
 * @deprecated будет заменен на новую логику
 */
@Controller
@Deprecated
public class ColumnDisplaySettingController {

    private final ColumnDisplaySettingService columnDisplaySettingService;

    public ColumnDisplaySettingController(ColumnDisplaySettingService columnDisplaySettingService) {
        this.columnDisplaySettingService = columnDisplaySettingService;
    }

    @GetMapping("/column/list/ops/load")
    @ResponseBody
    public List<ColumnDisplaySetting> column_list_ops_load(
        HttpSession session,
        ModelMap model,
        String tableId,
        @RequestParam(value = "columnNameList") List<String> columnNameList
    ) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        List<ColumnDisplaySetting> columnDisplaySettingList = columnDisplaySettingService.getColumnSettingList(tableId, user);
        // Удаляем несуществующие
        columnDisplaySettingList.stream().filter(setting -> !columnNameList.contains(setting.getName())).forEach(columnDisplaySettingService::delete);
        // Добавляем новые
        List<ColumnDisplaySetting> cdsList = IntStream.range(0, columnNameList.size())
            .mapToObj(i -> {
                String name = columnNameList.get(i);
                ColumnDisplaySetting columnDisplaySetting = columnDisplaySettingService.getSettingByName(tableId, name, user);
                if (columnDisplaySetting == null) {
                    columnDisplaySetting = new ColumnDisplaySetting();
                    columnDisplaySetting.setName(name);
                    columnDisplaySetting.setOrder(i);
                    columnDisplaySetting.setTableId(tableId);
                    columnDisplaySetting.setUser(user);
                    columnDisplaySetting.setToggle(Boolean.TRUE);
                }
                return columnDisplaySetting;
            }).collect(Collectors.toList());
        columnDisplaySettingService.saveAll(cdsList);

        return columnDisplaySettingService.getColumnSettingList(tableId, user);
    }

    @PostMapping("/column/list/ops/toggle")
    @ResponseBody
    public void column_list_ops_toggle(
        HttpSession session,
        ModelMap model,
        String tableId,
        String name
    ) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        ColumnDisplaySetting columnDisplaySetting = columnDisplaySettingService.getSettingByName(tableId, name, user);
        columnDisplaySetting.setToggle(!columnDisplaySetting.getToggle());
        columnDisplaySettingService.save(columnDisplaySetting);
    }

    @PostMapping("/column/list/ops/order")
    @ResponseBody
    public void column_list_ops_order(
        HttpSession session,
        ModelMap model,
        String tableId,
        @RequestParam(value = "columnNameList") List<String> columnNameList
    ) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        List<ColumnDisplaySetting> cdsList = IntStream.range(0, columnNameList.size())
            .mapToObj(i -> {
                ColumnDisplaySetting columnDisplaySetting = columnDisplaySettingService.getSettingByName(tableId, columnNameList.get(i), user);
                columnDisplaySetting.setOrder(i);
                return columnDisplaySetting;
            }).collect(Collectors.toList());
        columnDisplaySettingService.saveAll(cdsList);
    }

    @PostMapping(
        value = "/resetSettings",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE
        }
    )
    @ResponseBody
    public void resetSettings(
        HttpSession session,
        ModelMap model,
        String tableId
    ) {
        User user = KtCommonUtil.INSTANCE.getUser(session);
        columnDisplaySettingService.resetSettings(user, tableId);
    }
}