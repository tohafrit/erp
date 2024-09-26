<div class="ui modal">
    <div class="ui small header">Фильтр</div>
    <div class="content">
        <form class="ui small form">
            <div class="two fields">
                <div class="field">
                    <label>Извещение</label>
                    <div class="ui input std-div-input-search">
                        <input type="text" name="notice"/>
                    </div>
                </div>
                <div class="two fields">
                    <div class="field">
                        <label>Дата извещения с</label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="noticeDateFrom"/>
                            </div>
                        </div>
                    </div>
                    <div class="field">
                        <label><fmt:message key="label.to"/></label>
                        <div class="ui calendar">
                            <div class="ui input left icon std-div-input-search">
                                <i class="calendar icon"></i>
                                <input type="text" class="std-date" name="noticeDateTo"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="field">
                <label>Изделие</label>
                <div class="ui input std-div-input-search">
                    <input type="text" name="product"/>
                </div>
            </div>
            <div class="two fields">
                <div class="field">
                    <label>Пройден ОТК</label>
                    <select name="techControlled" class="ui dropdown std-select">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <option value="false"><fmt:message key="text.no"/></option>
                        <option value="true"><fmt:message key="text.yes"/></option>
                    </select>
                </div>
                <div class="field">
                    <label>Упаковано</label>
                    <select name="packed" class="ui dropdown std-select">
                        <option value=""><fmt:message key="text.notSpecified"/></option>
                        <option value="false"><fmt:message key="text.no"/></option>
                        <option value="true"><fmt:message key="text.yes"/></option>
                    </select>
                </div>
            </div>
        </form>
    </div>
</div>