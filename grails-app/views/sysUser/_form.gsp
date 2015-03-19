<%@ page import="ikakara.orguserteam.dao.dynamo.IdUser" %>
<input type="hidden" id="curalias" name="curalias" value="${userInstance.aliasId}">

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'id', 'error')} required">
  <label for="id">
    <g:message code="IdUser.id.label" default="Id" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="id" type="text" required="" value="${userInstance?.id}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'aliasId', 'error')} required">
  <label for="aliasId">
    <g:message code="IdUser.aliasId.label" default="Alias" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasId" type="text" value="${userInstance?.aliasId}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: userInstance, field: 'aliasPrefix', 'error')} required">
  <label for="aliasPrefix">
    <g:message code="IdUser.aliasPrefix.label" default="Prefix" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasPrefix" type="text" value="${userInstance?.aliasPrefix}" required=""/>
</div-->
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'name', 'error')} required">
  <label for="name">
    <g:message code="IdUser.name.label" default="Name" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="name" type="text" value="${userInstance?.name}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'initials', 'error')} required">
  <label for="initials">
    <g:message code="IdUser.initials.label" default="Initials" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="initials" type="text" value="${userInstance?.initials}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'description', 'error')} required">
  <label for="description">
    <g:message code="IdUser.description.label" default="Description" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="description" type="text" value="${userInstance?.description}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'status', 'error')} required">
  <label for="status">
    <g:message code="IdUser.status.label" default="Status" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="status" type="number" value="${userInstance?.status}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'imageUrl', 'error')} required">
  <label for="imageUrl">
    <g:message code="IdUser.imageUrl.label" default="Image Url" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="imageUrl" type="text" value="${userInstance?.imageUrl}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: userInstance, field: 'created_time', 'error')} required">
  <label for="created_time">
    <g:message code="IdUser.created_time.label" default="Created" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="createdDate" precision="hour"  value="${userInstance?.createdDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: userInstance, field: 'updated_time', 'error')} required">
  <label for="updated_time">
    <g:message code="IdUser.updated_time.label" default="Updated" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="updatedDate" precision="hour"  value="${userInstance?.updatedDate}"  />
</div-->
