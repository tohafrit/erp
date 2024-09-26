<div class="ui modal">
    <div class="header">Фильтр государственных контрактов</div>
    <div class="content">
        <form class="ui small form">
            <div class="field">
                <label>Идентификатор</label>
                <input type="search" name="identifier"/>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Заключен с</label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="dateFrom"/>
                        </div>
                    </div>
                </div>
                <div class="field">
                    <label><fmt:message key="label.to"/></label>
                    <div class="ui calendar">
                        <div class="ui input left icon">
                            <i class="calendar icon"></i>
                            <input type="text" class="std-date" name="dateTo"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Комментарий</label>
                <input type="search" name="comment"/>
            </div>
        </form>
    </div>
</div>