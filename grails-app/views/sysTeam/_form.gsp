<%@ page import="ikakara.orguserteam.dao.dynamo.IdTeam" %>
<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'id', 'error')} required">
  <label for="id">
    <g:message code="IdTeam.id.label" default="Id" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="id" required="" value="${teamInstance?.id}"/>
</div>
<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'aliasId', 'error')} required">
  <label for="aliasId">
    <g:message code="IdTeam.aliasId.label" default="Alias" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasId" type="tel" value="${teamInstance?.aliasId}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'aliasPrefix', 'error')} required">
  <label for="aliasPrefix">
    <g:message code="IdTeam.aliasPrefix.label" default="Prefix" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="aliasPrefix" type="tel" value="${teamInstance?.aliasPrefix}" required=""/>
</div-->


<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'privacy', 'error')} required">
  <label for="privacy">
    <g:message code="IdTeam.privacy.label" default="Privacy" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="privacy" type="number" value="${teamInstance?.privacy}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'imageUrl', 'error')} required">
  <label for="imageUrl">
    <g:message code="IdTeam.imageUrl.label" default="Image Url" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="imageUrl" type="tel" value="${teamInstance?.imageUrl}" required=""/>
</div>
<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'name', 'error')} required">
  <label for="name">
    <g:message code="IdTeam.name.label" default="Name" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="name" type="text" value="${teamInstance?.name}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'description', 'error')} required">
  <label for="description">
    <g:message code="IdTeam.description.label" default="Description" />
    <span class="required-indicator">*</span>
  </label>
  <g:field name="description" value="${teamInstance?.description}" required=""/>
</div>

<!--div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'created_time', 'error')} required">
  <label for="created_time">
    <g:message code="IdTeam.created_time.label" default="Created" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="createdDate" precision="hour"  value="${teamInstance?.createdDate}"  />
</div>

<div class="fieldcontain ${hasErrors(bean: teamInstance, field: 'updated_time', 'error')} required">
  <label for="updated_time">
    <g:message code="IdTeam.updated_time.label" default="Updated" />
    <span class="required-indicator">*</span>
  </label>
  <g:datePicker name="updatedDate" precision="hour"  value="${teamInstance?.updatedDate}"  />
</div-->
