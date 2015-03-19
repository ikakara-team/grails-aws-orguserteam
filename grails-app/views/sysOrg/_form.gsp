<%@ page import="ikakara.orguserteam.dao.dynamo.IdOrg" %>
<input type="hidden" id="curalias" name="curalias" value="${orgInstance.aliasId}">
<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'id', 'error')} required">
  <label for="id">
    <g:message code="IdOrg.id.label" default="Id" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="id" required="" value="${orgInstance?.id}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'aliasId', 'error')} required">
  <label for="aliasId">
    <g:message code="IdOrg.aliasId.label" default="Alias" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasId" type="text" value="${orgInstance?.aliasId}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'aliasPrefix', 'error')} required">
  <label for="aliasPrefix">
    <g:message code="IdOrg.aliasPrefix.label" default="Prefix" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasPrefix" type="text" value="${orgInstance?.aliasPrefix}" required=""/>
</div-->

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'visibility', 'error')} required">
  <label for="visibility">
    <g:message code="IdOrg.visibility.label" default="Visibility" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="visibility" type="number" value="${orgInstance?.visibility}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'name', 'error')} required">
  <label for="name">
    <g:message code="IdOrg.name.label" default="Name" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="name" type="text" value="${orgInstance?.name}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'description', 'error')} required">
  <label for="description">
    <g:message code="IdOrg.description.label" default="Description" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="description" value="${orgInstance?.description}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'webUrl', 'error')} required">
  <label for="webUrl">
    <g:message code="IdOrg.webUrl.label" default="Web Url" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="webUrl" type="text" value="${orgInstance?.webUrl}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'imageUrl', 'error')} required">
  <label for="imageUrl">
    <g:message code="IdOrg.imageUrl.label" default="Image Url" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="imageUrl" type="text" value="${orgInstance?.imageUrl}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'created_time', 'error')} required">
  <label for="created_time">
    <g:message code="IdOrg.created_time.label" default="Created" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="createdDate" precision="hour"  value="${orgInstance?.createdDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: orgInstance, field: 'updated_time', 'error')} required">
  <label for="updated_time">
    <g:message code="IdOrg.updated_time.label" default="Updated" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="updatedDate" precision="hour"  value="${orgInstance?.updatedDate}"  />
</div-->
