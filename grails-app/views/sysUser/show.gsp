
<%@ page import="ikakara.orguserteam.dao.dynamo.IdUser" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityName" value="${message(code: 'idUser.label', default: 'IdUser')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <a href="#show-idUser" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="navmenu" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </ul>
      </div>
      <div id="show-idUser" class="content scaffold-show" imageUrl="main">
        <h1><g:message code="default.show.label" args="[entityName]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="ISO3166">${flash.message}</div>
      </g:if>
      <ol class="property-list idUser">
        <g:if test="${userInstance?.id}">
          <li class="fieldcontain">
            <span id="id-label" class="property-label"><g:message code="idUser.id.label" default=" Id" /></span>
            <span class="property-value" aria-labelledby="id-label"><g:fieldValue bean="${userInstance}" field="id"/></span>
          </li>
        </g:if>
        <g:if test="${userInstance?.aliasId}">
          <li class="fieldcontain">
            <span id="aliasId-label" class="property-label"><g:message code="idUser.aliasId.label" default="Alias" /></span>
            <span class="property-value" aria-labelledby="aliasId-label"><g:fieldValue bean="${userInstance}" field="aliasId"/></span>
          </li>
        </g:if>

        <g:if test="${userInstance?.name}">
          <li class="fieldcontain">
            <span id="name-label" class="property-label"><g:message code="idUser.name.label" default="Name" /></span>
            <span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${userInstance}" field="name"/></span>
          </li>
        </g:if>
        <g:if test="${userInstance?.initials}">
          <li class="fieldcontain">
            <span id="initials-label" class="property-label"><g:message code="idUser.initials.label" default="Initials" /></span>
            <span class="property-value" aria-labelledby="initials-label"><g:fieldValue bean="${userInstance}" field="initials"/></span>
          </li>
        </g:if>
        <g:if test="${userInstance?.description}">
          <li class="fieldcontain">
            <span id="description-label" class="property-label"><g:message code="idUser.description.label" default="Description" /></span>
            <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${userInstance}" field="description"/></span>
          </li>
        </g:if>
        <g:if test="${userInstance?.status}">
          <li class="fieldcontain">
            <span id="status-label" class="property-label"><g:message code="idUser.status.label" default="Status" /></span>
            <span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${userInstance}" field="status"/></span>
          </li>
        </g:if>
        <g:if test="${userInstance?.imageUrl}">
          <li class="fieldcontain">
            <span id="imageUrl-label" class="property-label"><g:message code="idUser.imageUrl.label" default="Image Url" /></span>
            <span class="property-value" aria-labelledby="imageUrl-label"><g:fieldValue bean="${userInstance}" field="imageUrl"/></span>
          </li>
        </g:if>

        <g:if test="${userInstance?.createdTime}">
          <li class="fieldcontain">
            <span id="created_time-label" class="property-label"><g:message code="idUser.created_time.label" default="Created" /></span>
            <span class="property-value" aria-labelledby="created_time-label"><g:fieldValue bean="${userInstance}" field="createdTime"/></span>
          </li>
        </g:if>

        <g:if test="${userInstance?.updatedTime}">
          <li class="fieldcontain">
            <span id="updated_time-label" class="property-label"><g:message code="idUser.updated_time.label" default="Updated" /></span>
            <span class="property-value" aria-labelledby="updated_time-label"><g:fieldValue bean="${userInstance}" field="updatedTime"/></span>
          </li>
        </g:if>

      </ol>
      <g:form action="delete" id="${userInstance?.id}" method="DELETE">
        <fieldset class="buttons">
          <g:link class="edit" action="edit" id="${userInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
