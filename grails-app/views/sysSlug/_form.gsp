<%@ page import="ikakara.orguserteam.dao.dynamo.IdSlug" %>
<div class="fieldcontain ${hasErrors(bean: slugInstance, field: 'id', 'error')} required">
  <label for="id">
    <g:message code="IdSlug.id.label" default="Id" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="id" required="" value="${slugInstance?.id}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: slugInstance, field: 'aliasId', 'error')} required">
  <label for="aliasId">
    <g:message code="IdSlug.aliasId.label" default="Alias" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasId" type="tel" value="${slugInstance?.aliasId}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: slugInstance, field: 'aliasPrefix', 'error')} required">
  <label for="aliasPrefix">
    <g:message code="IdSlug.aliasPrefix.label" default="Prefix" />
    <span class="required-indicator">*</span>
  </label>

  <g:select name="aliasPrefix" from="${['~','!','@']}" value="${slugInstance?.aliasPrefix}"
    noSelection="['':'-Choose type-']"/>
  <!--g:field name="aliasPrefix" type="tel" value="${slugInstance?.aliasPrefix}" required=""/-->
</div>


<div class="fieldcontain ${hasErrors(bean: slugInstance, field: 'status', 'error')} required">
  <label for="status">
    <g:message code="IdSlug.status.label" default="Status" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="status" type="number" value="${slugInstance?.status}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: slugInstance, field: 'created_time', 'error')} required">
  <label for="created_time">
    <g:message code="IdSlug.created_time.label" default="Created" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="createdDate" precision="hour"  value="${slugInstance?.createdDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: slugInstance, field: 'updated_time', 'error')} required">
  <label for="updated_time">
    <g:message code="IdSlug.updated_time.label" default="Updated" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="updatedDate" precision="hour"  value="${slugInstance?.updatedDate}"  />
</div-->
