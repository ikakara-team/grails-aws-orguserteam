# grails-aws-orguserteam

Description:
--------------
Grails plugin, for a "Org-User-Team" design pattern used by apps like Trello.com and implemented using AWS DyanmoDB.

Org - an abstraction to organize users/teams.  Organization members can view/join teams.
User - a user can create/join organizations and teams and invite others users to join organizations/teams.
Team - an abstraction to further group users around projects, venues, boards (Trello), etc

![Class Diagram](/grails-app/assets/images/OrgUserTeam.png?raw=true "Class Diagram")

AWS DynamoDB is a NOSQL store where 99% of the ops management is taken care of by AWS.  Developers don't need to worry about
scalability, reliability, durability, etc.  The 1% that developers do have to worry about is managing throughput/performance via
AWS console and/or SDK.  See AWS documentation for more information: http://aws.amazon.com/dynamodb/details/

Installation:
--------------
```
  plugins {
...
    compile ':aws-instance:0.3.3'
    compile ':aws-orguserteam:0.1'
...
  }
```

Usage:
--------------


Copyright & License:
--------------
Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.

```
Apache 2 License - http://www.apache.org/licenses/LICENSE-2.0
```

History:
--------------
0.1 - initial checkin