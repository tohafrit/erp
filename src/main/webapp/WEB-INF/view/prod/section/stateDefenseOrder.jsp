<link rel="stylesheet" type="text/css" href="<c:url value="/resources/style/prod/view/state-defense-order.css"/>">

<h1 class="b-heading"><fmt:message key="report.title"/></h1>

<form:form method="POST" target="_blank" action="/prod/state-defense-order/generation" cssClass="ui small form state-defense-order__report-form" enctype="multipart/form-data" cssStyle="width:460px;">
    <div class="field inline">
        <div class="ui checkbox">
            <input type="checkbox" name="db" class="state-defense-order__db"/>
            <label><strong>Использовать базу данных</strong></label>
        </div>
    </div>
    <div class="field">
        <label><fmt:message key="report.file.firstStep"/></label>
        <div class="ui action input">
            <input type="text" placeholder="<fmt:message key="report.file.firstStep"/>" class="state-defense-order__hint" readonly>
            <input type="file" name="firstStep" class="state-defense-order__first-step"/>
            <div class="ui icon button">
                <i class="attach icon"></i>
            </div>
        </div>
    </div>
    <div class="field required">
        <label><fmt:message key="report.file.secondStep"/></label>
        <div class="ui action input">
            <input type="text" placeholder="<fmt:message key="report.file.secondStep"/>" class="state-defense-order__hint" readonly>
            <input type="file" name="secondStep" class="state-defense-order__second-step"/>
            <div class="ui icon button">
                <i class="attach icon"></i>
            </div>
        </div>
    </div>
    <button class="ui small button b-btn b-btn-import" type="submit">
        <fmt:message key="label.button.generate"/>
    </button>
</form:form>

<script>
    $(() => {
        const $form = $('form.state-defense-order__report-form');
        const $first = $form.find('input.state-defense-order__first-step');
        const $second = $form.find('input.state-defense-order__second-step');
        const $database = $form.find('input.state-defense-order__db');
        const $hint = $form.find('input.state-defense-order__hint');
        const $iconButton = $form.find('.ui.icon.button');

        $hint.add($iconButton).on({
            'click': e => $(e.currentTarget).parent().find('input:file').trigger('click')
        });
        $first.add($second).on({
            'change': e => {
                const file = e.currentTarget.files[0];
                const name = file ? file.name : '';
                $(e.currentTarget).prev().val(name);
            }
        });
        $database.on({
            'change': () => {
                $first.add($first.prev()).val('');
                $first.closest('div.ui.action.input').toggleClass('disabled', $database.is(':checked'))
            }
        });

        $form.on({
            'submit': () => ($first.get(0).files.length !== 0 || $database.is(':checked')) && $second.get(0).files.length !== 0
        });
    });
</script>