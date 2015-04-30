<%@ page import="ikakara.orguserteam.dao.dynamo.IdOrg" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityNameC" value="${message(code: 'IdOrg.label', default: 'IdOrg')}" />
    <title><g:message code="default.list.label" args="[entityNameC]" /></title>
  </head>
  <body>
    <a href="#list-idOrg" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" url="[action:'create']"><g:message code="default.new.label" args="[entityNameC]" /></g:link></li>
        </ul>
      </div>
      <div id="list-idOrg" class="content scaffold-list" imageUrl="main">
        <h1><g:message code="default.list.label" args="[entityNameC]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" imageUrl="visibility">${flash.message}</div>
      </g:if>
      <table>
        <thead>
          <tr>
            <g:sortableColumn property="app_id" title="${message(code: 'idOrg.app_id.label', default: ' Id')}" />
            <g:sortableColumn property="aliasId" title="${message(code: 'idOrg.aliasId.label', default: 'Alias')}" />
            <g:sortableColumn property="aliasPrefix" title="${message(code: 'idOrg.aliasPrefix.label', default: 'Prefix')}" />
            <g:sortableColumn property="visibility" title="${message(code: 'idOrg.visibility.label', default: 'Visibility')}" />
            <g:sortableColumn property="name" title="${message(code: 'idOrg.name.label', default: 'Name')}" />
            <g:sortableColumn property="imageUrl" title="${message(code: 'idOrg.imageUrl.label', default: 'Image Url')}" />
            <g:sortableColumn property="webUrl" title="${message(code: 'idOrg.webUrl.label', default: 'Web Url')}" />
            <g:sortableColumn property="description" title="${message(code: 'idOrg.description.label', default: 'Description')}" />
            <g:sortableColumn property="created_time" title="${message(code: 'idOrg.created_time.label', default: 'Created')}" />
          </tr>
        </thead>
        <tbody>
          <g:each in="${orgList}" status="i" var="orgInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td><g:link action="show" id="${orgInstance?.id?.encodeAsHTML()}">${fieldValue(bean: orgInstance, field: "id")}</g:link></td>
              <td>${fieldValue(bean: orgInstance, field: "aliasId")}</td>
              <td>${fieldValue(bean: orgInstance, field: "aliasPrefix")}</td>
              <td>${fieldValue(bean: orgInstance, field: "visibility")}</td>
              <td>${fieldValue(bean: orgInstance, field: "name")}</td>
              <td>${fieldValue(bean: orgInstance, field: "imageUrl")}</td>
              <td>${fieldValue(bean: orgInstance, field: "webUrl")}</td>
              <td>${fieldValue(bean: orgInstance, field: "description")}</td>
              <td>${fieldValue(bean: orgInstance, field: "createdTime")}</td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <g:paginate total="${orgCount ?: 0}" />
      </div>
    </div>
  </body>
</html>
