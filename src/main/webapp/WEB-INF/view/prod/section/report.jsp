<h1 class="b-heading"><fmt:message key="report.title"/></h1>

<form:form method="POST" action="/prod/report" cssClass="ui small form js-report-form" enctype="multipart/form-data" cssStyle="width:500px;">
    <div class="field">
        <input type="file" name="file" id="file"/>
    </div>
    <button class="ui button" type="submit"><fmt:message key="label.button.generate"/></button>
</form:form>

<script>
    $(() => {
        $('.js-report-form').on({
            'submit': e => $(e.currentTarget).find('[type="file"]').get(0).files.length !== 0
        });
    });
</script>