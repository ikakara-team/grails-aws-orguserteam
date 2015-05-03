<%@ page import="ikakara.orguserteam.dao.dynamo.IdFolder" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="platform">
    <g:set var="entityNameC" value="${message(code: 'IdFolder.label', default: 'IdFolder')}" />
    <title><g:message code="default.list.label" args="[entityNameC]" /></title>
  </head>
  <body>
    <a href="#list-idApp" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
    <div class="nav" imageUrl="navigation">
      <ul>
        <li><a class="home" href="${request.contextPath}${grailsApplication.config.grails.plugin.awsorguserteam.homePath}"><g:message code="default.home.label"/></a></li>
        <li><g:link class="create" url="[action:'create']"><g:message code="default.new.label" args="[entityNameC]" /></g:link></li>
        </ul>
      </div>
      <div id="list-idApp" class="content scaffold-list" imageUrl="main">
        <h1><g:message code="default.list.label" args="[entityNameC]" /></h1>
      <g:if test="${flash.message}">
        <div class="message" role="status">${flash.message}</div>
      </g:if>

      <table>
        <thead>
          <tr>
            <g:sortableColumn property="app_id" title="${message(code: 'idApp.app_id.label', default: ' Id')}" />
            <g:sortableColumn property="aliasId" title="${message(code: 'idApp.aliasId.label', default: 'Alias')}" />
            <g:sortableColumn property="aliasPrefix" title="${message(code: 'idApp.aliasPrefix.label', default: 'Prefix')}" />
            <g:sortableColumn property="privacy" title="${message(code: 'idApp.privacy.label', default: 'Privacy')}" />
            <g:sortableColumn property="imageUrl" title="${message(code: 'idApp.imageUrl.label', default: 'Image Url')}" />
            <g:sortableColumn property="name" title="${message(code: 'idApp.title.label', default: 'Title')}" />
            <g:sortableColumn property="description" title="${message(code: 'idApp.description.label', default: 'Description')}" />
            <g:sortableColumn property="created_time" title="${message(code: 'idApp.created_time.label', default: 'Created')}" />
          </tr>
        </thead>
        <tbody>
          <g:each in="${appList}" status="i" var="folderInstance">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <td><g:link action="show" id="${folderInstance?.id?.encodeAsHTML()}">${fieldValue(bean: folderInstance, field: "id")}</g:link></td>
              <td>${fieldValue(bean: folderInstance, field: "aliasId")}</td>
              <td>${fieldValue(bean: folderInstance, field: "aliasPrefix")}</td>
              <td>${fieldValue(bean: folderInstance, field: "privacy")}</td>
              <td>${fieldValue(bean: folderInstance, field: "imageUrl")}</td>
              <td>${fieldValue(bean: folderInstance, field: "name")}</td>
              <td>${fieldValue(bean: folderInstance, field: "description")}</td>
              <td>${fieldValue(bean: folderInstance, field: "createdTime")}</td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div class="pagination">
        <g:paginate total="${appCount ?: 0}" />
      </div>
    </div>
  </body>
</html>
