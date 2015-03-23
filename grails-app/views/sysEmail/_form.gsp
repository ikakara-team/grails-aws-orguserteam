<%@ page import="ikakara.orguserteam.dao.dynamo.IdEmail" %>
<div class="fieldcontain ${hasErrors(bean: emailInstance, field: 'id', 'error')} required">
  <label for="id">
    <g:message code="IdEmail.id.label" default="Id" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="id" required="" value="${emailInstance?.id}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: emailInstance, field: 'aliasId', 'error')}">
  <label for="aliasId">
    <g:message code="IdEmail.aliasId.label" default="Alias" />
  </label>
  <g:field name="aliasId" type="tel" value="${emailInstance?.aliasId}" />
</div>

<div class="fieldcontain ${hasErrors(bean: emailInstance, field: 'status', 'error')} required">
  <label for="status">
    <g:message code="IdEmail.status.label" default="Status" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="status" type="number" value="${emailInstance?.status}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: emailInstance, field: 'created_time', 'error')} required">
  <label for="created_time">
    <g:message code="IdEmail.created_time.label" default="Created" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="createdDate" precision="hour"  value="${emailInstance?.createdDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: emailInstance, field: 'updated_time', 'error')} required">
  <label for="updated_time">
    <g:message code="IdEmail.updated_time.label" default="Updated" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="updatedDate" precision="hour"  value="${emailInstance?.updatedDate}"  />
</div-->