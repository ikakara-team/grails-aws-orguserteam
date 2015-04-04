# grails-aws-orguserteam

Description:
--------------
Grails plugin, for a "Org-User-Team" design pattern used by apps like Trello.com and implemented using AWS DyanmoDB.

* Org - an abstraction to organize users/teams.  Organization members can view/join teams.
* User - a user can create/join organizations and teams and invite other users to join organizations/teams.
* Team - an abstraction to further group users around projects, venues, boards (Trello), etc

![Class Diagram](/grails-app/assets/images/OrgUserTeam.png?raw=true "Class Diagram")

AWS DynamoDB is a NOSQL store where 99% of the ops management is taken care of by AWS.  Developers don't need to worry about
scalability, reliability, durability, etc.  The 1% that developers do have to worry about is managing throughput/performance via
AWS console and/or SDK.  See AWS documentation for more information: http://aws.amazon.com/dynamodb/details/

Installation:
--------------
```
  plugins {
...
    compile ':aws-instance:0.3.7'
    compile ':aws-orguserteam:0.4.1'
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
Orgs, Users and Teams use the homePath of "/".
```
grails {
  plugin {
    awsorguserteam {
      homePath = "/"
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

orgUserTeamService:
--------------
* Id
  * ```AIdBase findIdObjBySlugId(String slugId)```
  * ```boolean exist(AIdBase id)```
* User
  * ```IdUser user(String userId, instance=true)```
  * ```List<IdUserOrg> listUser(IdOrg org)```
  * ```List<IdUserTeam> listUser(IdTeam team)```
  * ```IdUser createUser(IdUser user, String name, String initials, String desc, String shortName)```
  * ```IdUser updateUser(IdUser user, String name, String initials, String desc, String shortName)```
  * ```boolean deleteUser(IdUser user)```
* Org
  * ```IdOrg org(String orgId, instance=true)```
  * ```IdOrg getOrg(IdTeam team)```
  * ```List<IdOrgTeam> listOrg(IdTeam team)```
  * ```List<IdUserOrg> listOrg(IdUser user)```
  * ```List<IdEmailOrg> listOrg(IdEmail email)```
  * ```IdOrg createOrg(IdUser user, String orgName, String orgDescription)```
  * ```IdSlug updateOrg(IdOrg org, String name, String desc, String web_url, String shortName)```
  * ```boolean deleteOrg(IdOrg org)```
* Team
  * ```IdTeam team(String teamId, instance=true)```
  * ```List<IdOrgTeam> listTeamVisible(IdOrg org, IdUser user)```
  * ```List<IdOrgTeam> listTeam(IdOrg org)```
  * ```List<IdUserTeam> listTeam(IdUser user)```
  * ```List<IdEmailTeam> listTeam(IdEmail email)```
  * ```List<IdOrg> listOrgTeams(IdUser user, String myOrgName)```
  * ```IdTeam createTeam(IdUser user, String teamName, Integer privacy, String orgId))```
  * ```IdSlug updateTeam(IdTeam team, String name, Integer privacy, String description, String shortName)```
  * ```boolean updateTeamOwner(IdTeam team, String orgId)```
  * ```boolean deleteTeam(IdTeam team)```
* Email
  * ```IdEmail email(String emailId, instance=true)```
  * ```List<IdEmailOrg> listEmail(IdOrg org)```
  * ```List<IdEmailTeam> listEmail(IdTeam team)```
  * ```IdEmail createEmail(String emailId, IdUser user=null)```
  * ```IdEmail updateEmail(IdEmail email, IdUser uesr)```
  * ```boolean deleteEmail(IdEmail email)```
  * ```List<AIdEmailGroup> listGroup(IdEmail email)```
  * ```boolean addUserToOrg(IdUser invitedBy, IdUser user, IdOrg org, String... roles)```
  * ```boolean addEmailToOrg(IdUser invitedBy, String invitedName, IdEmail email, IdOrg org)```
  * ```boolean addUserToTeam(IdUser invitedBy, IdUser user, IdTeam team, String... roles)```
  * ```boolean addEmailToTeam(IdUser invitedBy, String invitedName, IdEmail email, IdTeam team)```

Copyright & License:
--------------
Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.

```
Apache 2 License - http://www.apache.org/licenses/LICENSE-2.0
```

History:
--------------
```
0.4.1 - fix listTeamVisible
0.4   - listGroup
0.3.7 - fix invites; member groups
0.2.5 - add/invite users to join organizations/teams
0.1.1 - fix title
0.1   - initial checkin
```