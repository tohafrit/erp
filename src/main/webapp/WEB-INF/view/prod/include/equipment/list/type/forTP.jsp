<div class="field">
    <div class="ui toggle checkbox">
        <input type="checkbox" name="archive" <c:if test="${form.archive}">checked</c:if>>
        <label><fmt:message key="equipment.field.archive"/></label>
    </div>
</div>
<div class="field required">
    <label><fmt:message key="equipment.field.equipmentUnits"/></label>
    <table class="ui tiny blue table compact celled js-unit-table">
        <thead>
            <tr>
                <th><fmt:message key="equipment.field.areaName"/></th>
                <th><fmt:message key="equipment.field.serialNumber"/></th>
                <th><fmt:message key="equipment.field.inventoryNumber"/></th>
                <c:if test="${empty form.id}">
                    <th><fmt:message key="equipment.field.code"/></th>
                </c:if>
                <th class="center aligned">
                    <i class="icon add link blue js-add-unit" title="<fmt:message key="label.button.add"/>"></i>
                </th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${fn:length(form.equipmentUnitList) > 0}">
                <c:forEach items="${form.equipmentUnitList}" var="equipmentUnit" varStatus="status">
                    <tr>
                        <input type="hidden" data-list-form-attribute="equipmentUnitList.id" value="${equipmentUnit.id}">
                        <td>
                            <select data-list-form-attribute="equipmentUnitList.areaId">
                                <option value=""><fmt:message key="text.notSpecified"/></option>
                                <c:forEach items="${productionAreaList}" var="productionArea">
                                    <option value="${productionArea.id}"
                                        <c:if test="${equipmentUnit.areaId eq productionArea.id}">
                                            selected
                                        </c:if>
                                    >
                                        ${productionArea.formatCode} ${productionArea.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </td>
                        <td class="center aligned">
                            <div class="ui fluid input">
                                <input data-list-form-attribute="equipmentUnitList.serialNumber" type="text" value="${equipmentUnit.serialNumber}" />
                            </div>
                        </td>
                        <td class="center aligned">
                            <div class="ui fluid input">
                                <input data-list-form-attribute="equipmentUnitList.inventoryNumber" type="text" value="${equipmentUnit.inventoryNumber}" />
                            </div>
                        </td>
                        <c:if test="${empty form.id}">
                            <td class="center aligned">
                                ${equipmentUnit.code}
                            </td>
                        </c:if>
                        <td class="center aligned">
                            <i class="icon times link red js-remove-unit"></i>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
        </tbody>
    </table>
    <div class="ui compact message error" data-field="equipmentUnitList"></div>
</div>
<div class="field">
    <label><fmt:message key="equipment.field.producerName"/></label>
    <select class="ui dropdown" name="producer.id">
        <option value=""><fmt:message key="text.notSpecified"/></option>
        <c:forEach items="${producerList}" var="producer">
            <option value="${producer.id}"
                <c:if test="${form.producer.id eq producer.id}">
                    selected
                </c:if>
            >${producer.name}</option>
        </c:forEach>
    </select>
</div>
<div class="field required">
    <label><fmt:message key="equipment.field.name"/></label>
    <input type="text" name="name" id="name" value="${form.name}" />
    <div class="ui compact message error" data-field="name"></div>
</div>
<div class="field">
    <label><fmt:message key="equipment.field.model"/></label>
    <input type="text" name="model" id="model" value="${form.model}" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.shift"/></label>
    <input type="text" name="shift" id="shift" value="${form.shift}" data-inputmask-regex="[1-9]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.weight"/></label>
    <input name="weight" id="weight" value="${form.weight}" data-inputmask-regex="[0-9]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.voltage"/></label>
    <input name="voltage" id="voltage" value="${form.voltage}" placeholder="999/999" data-inputmask-regex="[0-9/]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.power"/></label>
    <input name="power" id="power" value="${form.power}" data-inputmask-regex="[0-9]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.dimensions"/></label>
    <div class="ui fluid input">
        <input name="dimensionsLength" id="dimensionsLength" value="${form.dimensionsLength}" data-inputmask-regex="[0-9]*" />
        <span style="position:relative;top:5px;">&nbsp;<fmt:message key="equipment.field.separator"/>&nbsp;</span>
        <input name="dimensionsDepth" id="dimensionsDepth" value="${form.dimensionsDepth}" data-inputmask-regex="[0-9]*" />
        <span style="position:relative;top:5px;">&nbsp;<fmt:message key="equipment.field.separator"/>&nbsp;</span>
        <input name="dimensionsWidth" id="dimensionsWidth" value="${form.dimensionsWidth}" data-inputmask-regex="[0-9]*" />
    </div>
</div>
<div class="ui horizontal divider header"><fmt:message key="equipment.field.compressedAir"/></div>
<div class="field">
    <label><fmt:message key="equipment.field.compressedAirPressure"/></label>
    <input name="compressedAirPressure" id="compressedAirPressure" value="${form.compressedAirPressure}" data-inputmask-regex="[0-9-]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.compressedAirConsumption"/></label>
    <input name="compressedAirConsumption" id="compressedAirConsumption" value="${form.compressedAirConsumption}" data-inputmask-regex="[0-9]*" />
</div>
<div class="ui horizontal divider header"><fmt:message key="equipment.field.extractor"/></div>
<div class="field">
    <label><fmt:message key="equipment.field.extractorVolume"/></label>
    <input name="extractorVolume" id="extractorVolume" value="${form.extractorVolume}" data-inputmask-regex="[0-9]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.extractorDiameter"/></label>
    <input name="extractorDiameter" id="extractorDiameter" value="${form.extractorDiameter}" data-inputmask-regex="[0-9]*" />
</div>
<div class="ui divider"></div>
<div class="field">
    <label><fmt:message key="equipment.field.nitrogen"/></label>
    <input name="nitrogenPressure" id="nitrogenPressure" value="${form.nitrogenPressure}" data-inputmask-regex="[0-9-]*" />
</div>
<div class="field">
    <label><fmt:message key="equipment.field.water"/></label>
    <select class="ui dropdown" name="water">
        <c:forEach items="${waterTypeList}" var="waterType">
            <option value="${waterType.type}"
                <c:if test="${form.water eq waterType.type}">
                    selected
                </c:if>
            ><fmt:message key="${waterType.property}"/></option>
        </c:forEach>
    </select>
</div>
<div class="field">
    <label><fmt:message key="equipment.field.sewerage"/></label>
    <std:trueOrFalse name="sewerage" value="${form.sewerage}"/>
</div>
<div class="field">
    <label><fmt:message key="equipment.field.link"/></label>
    <div class="ui action input">
        <input name="link" id="link" value="${form.link}"/>
        <button class="js-btn-link-edit ui button" type="button">
            <i class="fas fa-pencil-alt"></i>
        </button>
    </div>
</div>