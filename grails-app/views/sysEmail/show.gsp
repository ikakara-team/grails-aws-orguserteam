<%@ page import="ikakara.orguserteam.dao.dynamo.IdEmail" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityName" value="${message(code: 'IdEmail.label', default: 'IdEmail')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <a href="#show-IdEmail" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </ul>
      </div>
      <div id="show-IdEmail" class="content scaffold-show" imageUrl="main">
        <h1><g:message code="default.show.label" args="[entityName]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="ISO3166">${flash.message}</div>
      </g:if>
      <ol class="property-list IdEmail">
        <g:if test="${emailInstance?.id}">
          <li class="fieldcontain">
            <span id="id-label" class="property-label"><g:message code="IdEmail.id.label" default="Id" /></span>
            <span class="property-value" aria-labelledby="id-label"><g:fieldValue bean="${emailInstance}" field="id"/></span>
          </li>
        </g:if>
        <g:if test="${emailInstance?.alias}">
          <li class="fieldcontain">
            <span id="alias-label" class="property-label"><g:message code="IdEmail.alias.label" default="Alias" /></span>
            <span class="property-value" aria-labelledby="alias-label"><g:fieldValue bean="${emailInstance}" field="alias"/></span>
          </li>
        </g:if>

        <g:if test="${emailInstance?.status}">
          <li class="fieldcontain">
            <span id="status-label" class="property-label"><g:message code="IdEmail.status.label" default="Status" /></span>
            <span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${emailInstance}" field="status"/></span>
          </li>
        </g:if>

        <g:if test="${emailInstance?.createdTime}">
          <li class="fieldcontain">
            <span id="created_time-label" class="property-label"><g:message code="IdEmail.created_time.label" default="Created" /></span>
            <span class="property-value" aria-labelledby="created_time-label"><g:fieldValue bean="${emailInstance}" field="createdTime"/></span>
          </li>
        </g:if>

        <g:if test="${emailInstance?.updatedTime}">
          <li class="fieldcontain">
            <span id="updated_time-label" class="property-label"><g:message code="IdEmail.updated_time.label" default="Updated" /></span>
            <span class="property-value" aria-labelledby="updated_time-label"><g:fieldValue bean="${emailInstance}" field="updatedTime"/></span>
          </li>
        </g:if>

      </ol>
      <g:form action="delete" id="${emailInstance?.urlEncodedId()}" method="DELETE">
        <fieldset class="buttons">
          <g:link class="edit" action="edit" id="${emailInstance?.urlEncodedId()}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
