<div class="list_child-notification__header">
  <h1 class="list_child-notification__header_title">Дочерние извещения</h1>
</div>
<div class="list_child-notification__table-wrap">
  <div class="list_child-notification__table table-sm table-striped"></div>
</div>

<script>
  $(() => {
    const table = new Tabulator('div.list_child-notification__table', {
      data: JSON.parse('${std:escapeJS(childNotificationList)}'),
      height: '100%',
      layout: 'fitDataFill',
      dataTree: true,
      dataTreeStartExpanded: true,
      columns: [
        TABR_COL_ID,
        { title: 'Номер', field: TABR_FIELD.DOC_NUMBER },
        {
          title: 'Дата выпуска',
          field: TABR_FIELD.RELEASE_ON,
          hozAlign: 'center',
          width: 130,
          resizable: false
        },
        {
          title: 'Срок изменения',
          field: TABR_FIELD.TERM_CHANGE_ON,
          hozAlign: 'center',
          width: 130,
          resizable: false
        },
        { title: 'Причина', field: TABR_FIELD.REASON },
        {
          title: 'Указание о заделе',
          field: TABR_FIELD.RESERVE_INDICATION,
          resizable: false,
          hozAlign: 'center',
          formatter: 'lightMark'
        },
        { title: 'Указание о внедрении', field: TABR_FIELD.INTRODUCTION_INDICATION },
        { title: 'Изделие применяемости', field: TABR_FIELD.PRODUCT },
        { title: 'Ведущий изделия', field: TABR_FIELD.USER }
      ],
      rowContextMenu: row => {
        let id = row.getData().id;
        return [
          {
            label: '<i class="edit icon blue"></i><fmt:message key="label.menu.edit"/>',
            action: () => editRecord(id)
          },
          {
            label: '<i class="trash alternate outline icon blue"></i><fmt:message key="label.menu.delete"/>',
            action: () => deleteNotification(id)
          }
        ];
      },
    });

    // Скролл до строки и ее выбор по id
    function rowScrollSelect(id) {
      table.selectRow(id);
      table.scrollToRow(id, 'middle', false);
    }

    // Функция добавления/редактирования сущности
    function editRecord(id) {
      $.modalWindow({
        loadURL: VIEW_PATH.LIST_EDIT,
        loadData: { id: id },
        submitURL: ACTION_PATH.LIST_EDIT_SAVE,
        submitAsJson: true,
        onSubmitSuccess: resp => {
          if (id) {
            table.setPage(table.getPage()).then(() => rowScrollSelect(id));
          } else {
            const id = resp.attributes.id;
            if (id) {
              filterData = {};
              table.setSort(TABR_SORT_ID_DESC);
              table.setPage(1).then(() => rowScrollSelect(id));
            }
          }
        }
      });
    }

    // Функция удаления сущности
    function deleteNotification(id) {
      confirmDialog({
        title: 'Удаление сущности',
        message: 'Вы действительно хотите удалить сущность?',
        onAccept: () => $.ajax({
          method: 'DELETE',
          url: ACTION_PATH.LIST_DELETE + id,
          beforeSend: () => togglePreloader(true),
          complete: () => togglePreloader(false)
        }).done(() => table.setData())
      });
    }
  });
</script>