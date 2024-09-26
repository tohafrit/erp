<div class="ui modal list_create-package__main">
    <div class="header">Выбор протокола</div>
    <div class="content">
        <div class="ui form">
            <div class="grouped fields">
                <div class="field">
                    <div class="ui radio checkbox">
                        <input type="radio" name="protocol" value="1" checked="checked"/>
                        <label>Протокол БТМ</label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui radio checkbox">
                        <input type="radio" name="protocol" value="2"/>
                        <label>Протокол БТ</label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui radio checkbox">
                        <input type="radio" name="protocol" value="3"/>
                        <label>Протокол БТ83</label>
                    </div>
                </div>
                <div class="field">
                    <div class="ui radio checkbox">
                        <input type="radio" name="protocol" value="4"/>
                        <label>Протокол БТ01</label>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="actions">
        <button class="ui small button list_create-package__btn-select">
            <i class="icon blue check"></i>
            <fmt:message key="label.button.select"/>
        </button>
    </div>
</div>

<script>
    $(() => {
        const logRecordId = '${logRecordId}';
        //
        const $modal = $('div.list_create-package__main');
        const $selectBtn = $('button.list_create-package__btn-select');
        const $radioGroup = $modal.find('input[name="protocol"]');

        // Выбор пункта письма
        $selectBtn.on({
            'click': () => {
                let protocolVal = $radioGroup.filter(':checked').val();
                window.open('/api/action/prod/present-log-record/list/download-create-package?logRecordId=' + logRecordId + '&protocolVal=' + protocolVal, '_blank')
            }
        });
    });
</script>