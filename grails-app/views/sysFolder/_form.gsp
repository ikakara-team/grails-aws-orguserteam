<%@ page import="ikakara.orguserteam.dao.dynamo.IdFolder" %>
<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'id', 'error')} required">
  <label for="id">
    <g:message code="IdFolder.id.label" default="Id" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="id" required="" value="${folderInstance?.id}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'aliasId', 'error')} required">
  <label for="aliasId">
    <g:message code="IdFolder.aliasId.label" default="Alias" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasId" type="tel" value="${folderInstance?.aliasId}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'aliasPrefix', 'error')} required">
  <label for="aliasPrefix">
    <g:message code="IdFolder.aliasPrefix.label" default="Prefix" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasPrefix" type="tel" value="${folderInstance?.aliasPrefix}" required=""/>
</div-->


<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'privacy', 'error')} required">
  <label for="privacy">
    <g:message code="IdFolder.privacy.label" default="Privacy" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="privacy" type="number" value="${folderInstance?.privacy}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'imageUrl', 'error')} required">
  <label for="imageUrl">
    <g:message code="IdFolder.imageUrl.label" default="Image Url" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="imageUrl" type="tel" value="${folderInstance?.imageUrl}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'name', 'error')} required">
  <label for="name">
    <g:message code="IdFolder.name.label" default="Name" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="name" type="text" value="${folderInstance?.name}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'description', 'error')} required">
  <label for="description">
    <g:message code="IdFolder.description.label" default="Description" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="description" value="${folderInstance?.description}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'created_time', 'error')} required">
  <label for="created_time">
    <g:message code="IdFolder.created_time.label" default="Created" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="createdDate" precision="hour"  value="${folderInstance?.createdDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: folderInstance, field: 'updated_time', 'error')} required">
  <label for="updated_time">
    <g:message code="IdFolder.updated_time.label" default="Updated" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="updatedDate" precision="hour"  value="${folderInstance?.updatedDate}"  />
</div-->
