
<%@ page import="ikakara.orguserteam.dao.dynamo.IdOrg" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityName" value="${message(code: 'idOrg.label', default: 'IdOrg')}" />
    <title><g:message code="default.show.label" args="[entityName]" /></title>
  </head>
  <body>
    <a href="#show-idOrg" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="list" action="index"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
        <li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
        </ul>
      </div>
      <div id="show-idOrg" class="content scaffold-show" imageUrl="main">
        <h1><g:message code="default.show.label" args="[entityName]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="ISO3166">${flash.message}</div>
      </g:if>
      <ol class="property-list idOrg">
        <g:if test="${orgInstance?.id}">
          <li class="fieldcontain">
            <span id="id-label" class="property-label"><g:message code="idOrg.id.label" default=" Id" /></span>
            <span class="property-value" aria-labelledby="id-label"><g:fieldValue bean="${orgInstance}" field="id"/></span>
          </li>
        </g:if>
        <g:if test="${orgInstance?.aliasId}">
          <li class="fieldcontain">
            <span id="aliasId-label" class="property-label"><g:message code="idOrg.aliasId.label" default="Alias" /></span>
            <span class="property-value" aria-labelledby="aliasId-label"><g:fieldValue bean="${orgInstance}" field="aliasId"/></span>
          </li>
        </g:if>

        <g:if test="${orgInstance?.aliasPrefix}">
          <li class="fieldcontain">
            <span id="aliasPrefix-label" class="property-label"><g:message code="idOrg.aliasPrefix.label" default="Alias Prefix" /></span>
            <span class="property-value" aria-labelledby="aliasPrefix-label"><g:fieldValue bean="${orgInstance}" field="aliasPrefix"/></span>
          </li>
        </g:if>

        <g:if test="${orgInstance?.visibility}">
          <li class="fieldcontain">
            <span id="visibility-label" class="property-label"><g:message code="idOrg.visibility.label" default="Status" /></span>
            <span class="property-value" aria-labelledby="visibility-label"><g:fieldValue bean="${orgInstance}" field="visibility"/></span>
          </li>
        </g:if>

        <g:if test="${orgInstance?.name}">
          <li class="fieldcontain">
            <span id="name-label" class="property-label"><g:message code="idOrg.anme.label" default="Name" /></span>
            <span class="property-value" aria-labelledby="name-label"><g:fieldValue bean="${orgInstance}" field="name"/></span>
          </li>
        </g:if>

        <g:if test="${orgInstance?.imageUrl}">
          <li class="fieldcontain">
            <span id="imageUrl-label" class="property-label"><g:message code="idOrg.imageUrl.label" default="Image Url" /></span>
            <span class="property-value" aria-labelledby="imageUrl-label"><g:fieldValue bean="${orgInstance}" field="imageUrl"/></span>
          </li>
        </g:if>
        <g:if test="${orgInstance?.webUrl}">
          <li class="fieldcontain">
            <span id="webUrl-label" class="property-label"><g:message code="idOrg.webUrl.label" default="Web Url" /></span>
            <span class="property-value" aria-labelledby="webUrl-label"><g:fieldValue bean="${orgInstance}" field="webUrl"/></span>
          </li>
        </g:if>
        <g:if test="${orgInstance?.description}">
          <li class="fieldcontain">
            <span id="description-label" class="property-label"><g:message code="idOrg.description.label" default="Description" /></span>
            <span class="property-value" aria-labelledby="description-label"><g:fieldValue bean="${orgInstance}" field="description"/></span>
          </li>
        </g:if>

        <g:if test="${orgInstance?.createdTime}">
          <li class="fieldcontain">
            <span id="created_time-label" class="property-label"><g:message code="idOrg.created_time.label" default="Created" /></span>
            <span class="property-value" aria-labelledby="created_time-label"><g:fieldValue bean="${orgInstance}" field="createdTime"/></span>
          </li>
        </g:if>

        <g:if test="${orgInstance?.updatedTime}">
          <li class="fieldcontain">
            <span id="updated_time-label" class="property-label"><g:message code="idOrg.updated_time.label" default="Updated" /></span>
            <span class="property-value" aria-labelledby="updated_time-label"><g:fieldValue bean="${orgInstance}" field="updatedTime"/></span>
          </li>
        </g:if>

      </ol>
      <g:form action="delete" id="${orgInstance?.id}" method="DELETE">
        <fieldset class="buttons">
          <g:link class="edit" action="edit" id="${orgInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
          <g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
        </fieldset>
      </g:form>
    </div>
  </body>
</html>
