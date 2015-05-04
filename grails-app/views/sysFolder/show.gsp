<%@ page import="ikakara.orguserteam.dao.dynamo.IdFolder" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityName" value="${message(code: 'idApp.label', default: 'IdFolder')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <a href="#show-idApp" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </ul>
      </div>
      <div id="show-idApp" class="content scaffold-show" imageUrl="main">
        <h1><g:message code="default.show.label" args="[entityName]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="ISO3166">${flash.message}</div>
      </g:if>
      <ol class="property-list idApp">
        <g:if test="${folderInstance?.id}">
          <li class="fieldcontain">
            <span id="id-label" class="property-label"><g:message code="idApp.id.label" default=" Id" /></span>
            <span class="property-value" aria-labelledby="id-label"><g:fieldValue bean="${folderInstance}" field="id"/></span>
          </li>
        </g:if>
        <g:if test="${folderInstance?.aliasId}">
          <li class="fieldcontain">
            <span id="aliasId-label" class="property-label"><g:message code="idApp.aliasId.label" default="Alias" /></span>
            <span class="property-value" aria-labelledby="aliasId-label"><g:fieldValue bean="${folderInstance}" field="aliasId"/></span>
          </li>
        </g:if>

        <g:if test="${folderInstance?.aliasPrefix}">
          <li class="fieldcontain">
            <span id="aliasPrefix-label" class="property-label"><g:message code="idApp.aliasPrefix.label" default="Alias Prefix" /></span>
            <span class="property-value" aria-labelledby="aliasPrefix-label"><g:fieldValue bean="${folderInstance}" field="aliasPrefix"/></span>
          </li>
        </g:if>

        <g:if test="${folderInstance?.privacy}">
          <li class="fieldcontain">
            <span id="privacy-label" class="property-label"><g:message code="idApp.privacy.label" default="Privacy" /></span>
            <span class="property-value" aria-labelledby="privacy-label"><g:fieldValue bean="${folderInstance}" field="privacy"/></span>
          </li>
        </g:if>
        <g:if test="${folderInstance?.imageUrl}">
          <li class="fieldcontain">
            <span id="imageUrl-label" class="property-label"><g:message code="idApp.imageUrl.label" default="Image Url" /></span>
            <span class="property-value" aria-labelledby="imageUrl-label"><g:fieldValue bean="${folderInstance}" field="imageUrl"/></span>
          </li>
        </g:if>
        <g:if test="${folderInstance?.name}">
          <li class="fieldcontain">
            <span id="name-label" class="property-label"><g:message code="idApp.name.label" default="Title" /></span>
            <span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${folderInstance}" field="name"/></span>
          </li>
        </g:if>
        <g:if test="${folderInstance?.description}">
          <li class="fieldcontain">
            <span id="description-label" class="property-label"><g:message code="idApp.description.label" default="Description" /></span>
            <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${folderInstance}" field="description"/></span>
          </li>
        </g:if>

        <g:if test="${folderInstance?.createdTime}">
          <li class="fieldcontain">
            <span id="created_time-label" class="property-label"><g:message code="idApp.created_time.label" default="Created" /></span>
            <span class="property-value" aria-labelledby="created_time-label"><g:fieldValue bean="${folderInstance}" field="createdTime"/></span>
          </li>
        </g:if>

        <g:if test="${folderInstance?.updatedTime}">
          <li class="fieldcontain">
            <span id="updated_time-label" class="property-label"><g:message code="idApp.updated_time.label" default="Updated" /></span>
            <span class="property-value" aria-labelledby="updated_time-label"><g:fieldValue bean="${folderInstance}" field="updatedTime"/></span>
          </li>
        </g:if>

      </ol>
      <g:form action="delete" id="${folderInstance?.id}" method="DELETE">
        <fieldset class="buttons">
          <g:link class="edit" action="edit" id="${folderInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
