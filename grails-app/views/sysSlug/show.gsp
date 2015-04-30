<%@ page import="ikakara.orguserteam.dao.dynamo.IdSlug" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityName" value="${message(code: 'idSlug.label', default: 'IdSlug')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <a href="#show-idSlug" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </ul>
      </div>
      <div id="show-idSlug" class="content scaffold-show" imageUrl="main">
        <h1><g:message code="default.show.label" args="[entityName]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="ISO3166">${flash.message}</div>
      </g:if>
      <ol class="property-list idSlug">
        <g:if test="${slugInstance?.id}">
          <li class="fieldcontain">
            <span id="id-label" class="property-label"><g:message code="idSlug.id.label" default="Id" /></span>
            <span class="property-value" aria-labelledby="id-label"><g:fieldValue bean="${slugInstance}" field="id"/></span>
          </li>
        </g:if>
        <g:if test="${slugInstance?.aliasId}">
          <li class="fieldcontain">
            <span id="aliasId-label" class="property-label"><g:message code="idSlug.aliasId.label" default="Alias" /></span>
            <span class="property-value" aria-labelledby="aliasId-label"><g:fieldValue bean="${slugInstance}" field="aliasId"/></span>
          </li>
        </g:if>

        <g:if test="${slugInstance?.aliasPrefix}">
          <li class="fieldcontain">
            <span id="aliasPrefix-label" class="property-label"><g:message code="idSlug.aliasPrefix.label" default="Alias Prefix" /></span>
            <span class="property-value" aria-labelledby="aliasPrefix-label"><g:fieldValue bean="${slugInstance}" field="aliasPrefix"/></span>
          </li>
        </g:if>

        <g:if test="${slugInstance?.status}">
          <li class="fieldcontain">
            <span id="status-label" class="property-label"><g:message code="idSlug.status.label" default="Status" /></span>
            <span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${slugInstance}" field="status"/></span>
          </li>
        </g:if>

        <g:if test="${slugInstance?.createdTime}">
          <li class="fieldcontain">
            <span id="created_time-label" class="property-label"><g:message code="idSlug.created_time.label" default="Created" /></span>
            <span class="property-value" aria-labelledby="created_time-label"><g:fieldValue bean="${slugInstance}" field="createdTime"/></span>
          </li>
        </g:if>

        <g:if test="${slugInstance?.updatedTime}">
          <li class="fieldcontain">
            <span id="updated_time-label" class="property-label"><g:message code="idSlug.updated_time.label" default="Updated" /></span>
            <span class="property-value" aria-labelledby="updated_time-label"><g:fieldValue bean="${slugInstance}" field="updatedTime"/></span>
          </li>
        </g:if>

      </ol>
      <g:form action="delete" id="${slugInstance?.urlEncodedId()}" method="DELETE">
        <fieldset class="buttons">
          <g:link class="edit" action="edit" id="${slugInstance?.urlEncodedId()}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
