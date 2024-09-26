<div class="ui modal">
    <div class="header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="two fields">
                <div class="field">
                    <label>Утвержден с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="approvalDateTo"/>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>