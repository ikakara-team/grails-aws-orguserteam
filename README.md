# grails-aws-orguserteam

Team is now Folder to disambiguate its relationship from Orgs and Users.

Org-User-Folder is a very common "design pattern" used in just about every SAAS
app.  In the past, the relationships would be easily represented in SQL.  However,
this meant that you would have to setup/configure/maintain a DB.  Even in the cloud,
there is still devops work.  So welcome to modern development where services and 
plugins are the building blocks of apps, rather than libraries.

There are tradeoffs with using NOSQL (service) vs SQL (box):  
1) Boxes require devops!  Prefer services to boxes :)
2) NOSQL sucks at relationships ... Prefer services to boxes!!!

This plugin takes care of the suckiness of NOSQL, in preference to keeping devops
to a minimum.

Example Application:
--------------
See <a href="https://github.com/ikakara-team/grails-example-orguserteam">example application</a>

Description:
--------------
Grails plugin, for a "Org-User-Folder" design pattern used by apps like Trello.com and implemented using AWS DyanmoDB.

* Account - owns Groups.  Users and organizations are accounts.
  * Each account has 1 and only 1 owner (linked to the account)
* Group   - contains members.  Organizations and folders are groups.
  * Each member (of a group) can have a group role, such as 'owner' or 'admin.'
  * Roles and their effect on visibility, access, etc are developer defined.
* User    - can create/join organizations and folders and invite other users to join organizations/folders.
* Org     - an abstraction to organize users/folders.  Organization members can view/join folders.
  * Visibility (to other users) is private (default) or public. Only members can update the Org.
  * Only (org) owner can delete the Org which will delete all folders owned by the Org.
* Folder  - a collection to further group users around projects, venues, boards (Trello), etc
  * Visibility (to other users) is private (default), organizational or public. Only members can update the Folder.
  * Folder owner can delete the Folder.

![Class Diagram](/grails-app/assets/images/OrgUserFolder.png?raw=true "Class Diagram")

AWS DynamoDB is a NOSQL store where 99% of the ops management is taken care of by AWS.  Developers don't need to worry about
scalability, reliability, durability, etc.  The 1% that developers do have to worry about is managing throughput/performance via
AWS console and/or SDK.  See AWS documentation for more information: http://aws.amazon.com/dynamodb/details/

Installation:
--------------
```
  plugins {
...
    compile ':aws-instance:0.6.1'
    compile ':aws-orguserteam:0.9.7'
...
  }
```

Configuration:
--------------
Add the following to grails-app/conf/Config.groovy:
```
grails {
  plugin {
    awsinstance {
      accessKey='AWS_ACCESSKEY'
      secretKey='AWS_SECRETKEY'
      s3.bucketName='AWS_S3_BUCKETNAME'
      ses.mailFrom='AWS_SES_MAILFROM'
    }
  }
}
```
See <a href="https://github.com/ikakara-team/grails-aws-instance">aws-instance README</a>

By default, automatically creates DynamoDB tables w/ "DEV" prefix.  Sys (admin) controllers to manage
Orgs, Users and Folders use the homePath of "/".
```
grails {
  plugin {
    awsorguserteam {
      homePath = "/"
      invalidAccessRedirectUri = "/welcome"
      userNotFoundRedirectUri = "/my-profile"
      defaultReturnUri = "/welcome"
      dataSource {
        dbPrefix="DEV"
        dbCreate="create" //'create', 'create-drop',''
      }
    }
  }
}
```

Usage:
--------------
This plugin includes 3 (abstract) base classes so use is DRY as possible:
* ABaseOrgController  - defines an interceptor to check for "org level access"
* ABaseFolderController - defines an interceptor to check for "folder level access"
* ABaseUserController - defines CRUD operations for invitations, orgs and folders

To use any of the base classes, developers will need to define 4 (interface) methods:
* String getOrgSlugId()
* String getFolderSlugId()
* String getUserEmail()
* String getUserId()

For example,
```
import ikakara.orguserteam.web.app.ABaseUserController

class UserDashboardController extends ABaseUserController {
  // Get the following from your inputs
  String getOrgSlugId() {
    return params.id
  }

  String getFolderSlugId() {
    return params.folderId
  }

  // Get the following from your auth provider
  String getUserEmail() {
    return springSecurityService.principal?.email
  }

  String getUserId(){
    return springSecurityService.principal?.id
  }
```

To utilize UserDashboardController's inherited CRUD operations on invitations, orgs, folders,
add the following to your UrlMappings.groovy:
```
    "/my-invitations/$id?(.$format)?"(controller: "userDashboard", parseRequest: true) {
      action = [GET: "invitations", POST: "joinInvitation", DELETE: "deleteInvitation"]
    }
    "/my-orgs/$id?(.$format)?"(parseRequest: true) {
      controller = [GET: "userDashboard", PUT: "orgDashboard", POST: "userDashboard", DELETE: "orgDashboard"]
      action = [GET: "orgs", PUT: "updateOrg", POST: "saveOrg", DELETE: "deleteOrg"]
    }
    "/my-folders/$id?(.$format)?"(parseRequest: true) {
      controller = [GET: "userDashboard", PUT: "orgDashboard", POST: "orgDashboard", DELETE: "orgDashboard"]
      action = [GET: "folders", PUT: "updateFolder", POST: "saveFolder", DELETE: "deleteFolder"]
    }
    "/my-groups(.$format)?"(controller: "userDashboard", action: "groups")
```

orgUserTeamService:
--------------
* Id
  * ```AIdBase findIdObjBySlugId(String slugId)```
  * ```boolean exist(AIdBase id)```
  * ```IdEmailOrg exist(IdEmail email, IdOrg org)```
  * ```IdEmailFolder exist(IdEmail email, IdFolder folder)```
* User
  * ```IdUser user(String userId, instance=true)```
  * ```List<IdUserOrg> listUser(IdOrg org)```
  * ```List<IdUserFolder> listUser(IdFolder folder)```
  * ```IdUser createUser(IdUser user, String name, String initials, String desc, String shortName)```
  * ```IdUser updateUser(IdUser user, String name, String initials, String desc, String shortName)```
  * ```boolean deleteUser(IdUser user)```
* Org
  * ```IdOrg org(String orgId, instance=true)```
  * ```boolean isOrgVisible(IdOrg org, IdUser user)```
  * ```List<IdOrgFolder> listOrg(IdFolder folder)```
  * ```List<IdUserOrg> listOrg(IdUser user)```
  * ```List<IdEmailOrg> listOrg(IdEmail email)```
  * ```IdOrg createOrg(IdUser user, String orgName, String orgDescription)```
  * ```IdSlug updateOrg(IdOrg org, String name, String desc, String web_url, String shortName, Integer visibility = null)```
  * ```boolean deleteOrg(IdOrg org)```
* Folder
  * ```IdFolder folder(String folderId, instance=true)```
  * ```boolean isFolderVisible(IdFolder folder, IdUser user, boolean orgMember)```
  * ```boolean isFolderVisible(IdFolder folder, IdUser user)```
  * ```boolean haveOrgRole(IdOrg org, IdUser user, Set orgRoles)```
  * ```boolean haveOrgRole(IdUserOrg orguser, Set orgRoles)```
  * ```List<IdOrgFolder> listFolderVisible(IdOrg org, IdUser user, Set orgRoles=null)```
  * ```List<IdOrgFolder> listFolder(IdOrg org)```
  * ```List<IdUserFolder> listFolder(IdUser user)```
  * ```List<IdEmailFolder> listFolder(IdEmail email)```
  * ```List<IdOrg> listFolderByAccount(IdUser user, String myAccountName)```
  * ```IdFolder createFolder(IdUser user, String folderName, Integer privacy, String orgId))```
  * ```IdFolder createFolder(IdUser user, String folderName, Integer privacy, IdOrg org)```
  * ```IdSlug updateFolder(IdFolder folder, String name, Integer privacy, String description, String shortName)```
  * ```boolean updateFolderOwner(IdFolder folder, AIdAccount newOwner)```
  * ```boolean deleteFolder(IdFolder folder)```
* Email
  * ```IdEmail email(String emailId, instance=true)```
  * ```List<IdEmailOrg> listEmail(IdOrg org)```
  * ```List<IdEmailFolder> listEmail(IdFolder folder)```
  * ```IdEmail createEmail(String emailId, IdUser user=null)```
  * ```IdEmail updateEmail(IdEmail email, IdUser uesr)```
  * ```boolean deleteEmail(IdEmail email)```
  * ```List<AIdUserGroup> listGroup(IdUser user)```
  * ```List<AIdEmailGroup> listGroup(IdEmail email)```
  * ```boolean addUserToGroup(IdUser invitedBy, IdUser user, IdOrg org, String... roles)```
  * ```boolean addEmailToGroup(IdUser invitedBy, String invitedName, IdEmail email, IdOrg org)```
  * ```boolean addUserToGroup(IdUser invitedBy, IdUser user, IdFolder folder, String... roles)```
  * ```boolean addEmailToGroup(IdUser invitedBy, String invitedName, IdEmail email, IdFolder folder)```

Copyright & License:
--------------
Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.

```
Apache 2 License - http://www.apache.org/licenses/LICENSE-2.0
```

Roadmap:
--------------
```
1.0.0 - Member access annotation (for controllers/actions) and configuration
1.1.0 - (Usage) Documentation
```

History:
--------------
```
0.9.7  - updateFolderOwner
0.9.6  - fix visibility
0.9.5  - fix access
0.9.4  - listFolderByAccount
0.9.3  - more refactoring
0.9.2  - refactor/rename Status
0.9.1  - fix for UpdateItemSpec
0.9.0  - update marshalItemOUT - breaking changes
0.8.6  - rename Team to Folder
0.7.8  - refactor access
0.6.10 - misc tweaks
0.5.7  - team visibility
0.4.1  - listGroup
0.3.7  - fix invites; member groups
0.2.5  - add/invite users to join organizations/teams
0.1.1  - fix title
0.1    - initial checkin
```