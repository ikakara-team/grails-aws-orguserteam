/* Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ikakara.orguserteam.dao.dynamo

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition
import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex
import com.amazonaws.services.dynamodbv2.model.Projection
import com.amazonaws.services.dynamodbv2.model.ProjectionType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.amazonaws.services.dynamodbv2.model.TableDescription

import ikakara.awsinstance.aws.AWSInstance
import ikakara.awsinstance.aws.DynamoHelper
import ikakara.awsinstance.dao.ITypeObject
import ikakara.awsinstance.dao.dynamo.ACreatedUpdatedObject
import ikakara.awsinstance.dao.dynamo.ADynamoObject

/**
 * @author Allen
 */
@DynamoDBTable(tableName = "MemberGroups")
@Slf4j("LOG")
@CompileStatic
abstract class AMemberGroupBase extends ACreatedUpdatedObject implements ITypeObject {

  private static String TABLE_NAME

  protected AIdBase member // mobile, email, hash
  protected AIdBase group
  protected IdUser  invitedBy
  @DynamoDBAttribute(attributeName = "MemberRole")
  String memberRole
  protected boolean bLoadMember = false
  protected boolean bLoadGroup = false

  @Override
  synchronized String tableName() {
    if (!TABLE_NAME) {
      TABLE_NAME = DynamoHelper.getTableName(AMemberGroupBase, "grails.plugin.awsorguserteam.dataSource")
      DynamoHelper.initTable(TABLE_NAME, this, "grails.plugin.awsorguserteam.dataSource")
    }
    return TABLE_NAME
  }

  @Override
  Map initTable() {
    Map map = DynamoHelper.getTableInformation(tableName())
    if (map == null) {
      // Table doesn't exist.  Let's create it.
      ProvisionedThroughput THRUPUT = new ProvisionedThroughput(1L, 1L)
      Projection PROJECTION = new Projection().withProjectionType(ProjectionType.ALL)
      Projection PROJECTION_TYPE = new Projection().withProjectionType(ProjectionType.KEYS_ONLY)

      CreateTableRequest req = new CreateTableRequest()
      .withTableName(tableName())
      .withAttributeDefinitions(
        new AttributeDefinition(nameHashKey(), ScalarAttributeType.S),
        new AttributeDefinition(nameRangeKey(), ScalarAttributeType.S),
        new AttributeDefinition("IdType", ScalarAttributeType.S),
        new AttributeDefinition("CreatedTime", ScalarAttributeType.S)
      )
      .withKeySchema(
        new KeySchemaElement(nameHashKey(), KeyType.HASH),
        new KeySchemaElement(nameRangeKey(), KeyType.RANGE))
      .withProvisionedThroughput(THRUPUT)
      .withLocalSecondaryIndexes(
        new LocalSecondaryIndex()
        .withIndexName("Idx_" + nameHashKey())
        .withKeySchema(
          new KeySchemaElement(nameHashKey(), KeyType.HASH),
          new KeySchemaElement("IdType", KeyType.RANGE))
        .withProjection(PROJECTION)
      )
      .withGlobalSecondaryIndexes(
        new GlobalSecondaryIndex()
        .withIndexName("Idx_IdType")
        .withKeySchema(
          new KeySchemaElement("IdType", KeyType.HASH),
          new KeySchemaElement("CreatedTime", KeyType.RANGE))
        .withProjection(PROJECTION_TYPE)
        .withProvisionedThroughput(THRUPUT))
      .withGlobalSecondaryIndexes(
        new GlobalSecondaryIndex()
        .withIndexName("Idx_" + nameRangeKey())
        .withKeySchema(
          new KeySchemaElement(nameRangeKey(), KeyType.HASH),
          new KeySchemaElement("IdType", KeyType.RANGE))
        .withProjection(PROJECTION)
        .withProvisionedThroughput(THRUPUT))

      Table table = AWSInstance.DYNAMO_DB().createTable(req)

      try {
        // Wait for the table to become active
        TableDescription desc = table.waitForActive()
        map = DynamoHelper.tableDescriptionToMap(desc)
      } catch (ie) {
        LOG.error("initTable $ie.message")
        map = DynamoHelper.getTableInformation(tableName())
      }

    }
    return map
  }

  @Override
  def valueHashKey() {
    member?.valueHashKey()
  }

  @Override
  String nameHashKey() {
    return "MemberId"
  }

  @Override
  def valueRangeKey() {
    group?.valueHashKey()
  }

  @Override
  String nameRangeKey() {
    return "GroupId"
  }

  @Override
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map) {
    if (item.isPresent("MemberId")) {
      memberId = item.getString("MemberId")
    }
    if (item.isPresent("GroupId")) {
      groupId = item.getString("GroupId")
    }
    if (item.isPresent("InvitedById")) {
      invitedById = item.getString("InvitedById")
    }
    if (item.isPresent("MemberRole")) {
      memberRole = item.getString("MemberRole")
    }
    //}
  }

  @Override
  Item marshalItemOUT(boolean removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()

    if (member != null) {
      outItem = outItem.withString("MemberId", (String) member.valueHashKey())
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("MemberId")
    }

    if (group) {
      outItem = outItem.withString("GroupId", (String) group.valueHashKey())
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("GroupId")
    }

    if (invitedBy) {
      outItem = outItem.withString("InvitedById", (String) invitedBy.valueHashKey())
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("InvitedById")
    }

    if (memberRole) {
      outItem = outItem.withString("MemberRole", memberRole)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("MemberRole")
    }

    if (type) {
      outItem = outItem.withString("IdType", type)
    } else if (removeAttributeNull) {
      outItem = outItem.removeAttribute("IdType")
    }

    return outItem
  }

  @Override
  ADynamoObject newInstance(Item item) {
    ADynamoObject obj

    if (item) {
      // this is hacky to store different configs into one table
      if (item.isPresent("IdType")) {
        String type = item.getString("IdType")
        switch (type) {
        case IdUserOrg.ID_TYPE:  obj = new IdUserOrg();  break
        case IdUserTeam.ID_TYPE: obj = new IdUserTeam(); break
        case IdOrgTeam.ID_TYPE:  obj = new IdOrgTeam();  break
        case IdEmailOrg.ID_TYPE:  obj = new IdEmailOrg();  break
        case IdEmailTeam.ID_TYPE: obj = new IdEmailTeam(); break
        }
      }
    }

    obj?.marshalAttributesIN(item)

    return obj
  }

  @DynamoDBIgnore
  @Override
  String getId() {
    return memberId + "_" + groupId
  }

  @Override
  void setId(String id) {
    if (!id) {
      return
    }

    String[] ids = id.split("_")
    if (ids.length < 2) {
      return
    }

    groupId = ids[1]
    // fix broken contactId
    String str = ids[0]
    if (str.startsWith(" ")) {
      str = str.replaceFirst(" ", "+")
    }
    memberId = str
  }

  AMemberGroupBase withMember(AIdBase mem) {
    member = mem
    return this
  }

  AMemberGroupBase withGroup(AIdBase grp) {
    group = grp
    return this
  }

  AMemberGroupBase withInvitedBy(IdUser user) {
    invitedBy = user
    return this
  }

  @DynamoDBIgnore
  AIdBase getMember() {
    if (member && !bLoadMember) {
      bLoadMember = member.load()
    }

    return member
  }

  @DynamoDBIgnore
  AIdBase getGroup() {
    if (group && !bLoadGroup) {
      bLoadGroup = group.load()
    }
    return group
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {

    String group_str = (String) params.group
    if (group_str != null) {
      groupId = group_str
    }

    String member_str = (String) params.member
    if (member_str != null) {
      memberId = member_str
    }

    memberRole = (String) params.member_role
    //}
  }

  @DynamoDBHashKey(attributeName = "MemberId")
  String getMemberId() {
    return (String) member.valueHashKey()
  }

  void setMemberId(String id) {
    member = AIdBase.toId(id)
  }

  @DynamoDBRangeKey(attributeName = "GroupId")
  String getGroupId() {
    return (String) group.valueHashKey()
  }

  void setGroupId(String id) {
    group = AIdBase.toId(id)
  }

  @DynamoDBAttribute(attributeName = "InvitedById")
  public String getInvitedById() {
    return (String) invitedBy?.valueHashKey()
  }

  public void setInvitedById(String id) {
    invitedBy = (IdUser)AIdBase.toId(id)
  }

  AMemberGroupBase() {
  }

  AMemberGroupBase(Map params) {
    initParameters(params)
  }

  List<AIdBase> _findByType() {
    // Scan items for IdType
    String where = "IdType = :myIdType"
    ValueMap valueMap = new ValueMap()
    .withString(":myIdType", type)
    scan(where, valueMap)
  }

  List<AMemberGroupBase> queryByType() {
    queryIndex("Idx_IdType", "IdType", type)
  }

  List<AMemberGroupBase> queryByMember() {
    LOG.info("hash:${nameHashKey()} value:${valueHashKey()}")
    query(nameHashKey(), valueHashKey())
  }

  List<AMemberGroupBase> queryByMemberAndType() {
    RangeKeyCondition rkc = new RangeKeyCondition("IdType").eq(type)
    queryIndex("Idx_" + nameHashKey(), nameHashKey(), valueHashKey(), rkc)
  }

  List<AMemberGroupBase> queryByGroup() {
    queryIndex("Idx_" + nameRangeKey(), nameRangeKey(), valueRangeKey())
  }

  List<AMemberGroupBase> queryByGroupAndType() {
    RangeKeyCondition rkc = new RangeKeyCondition("IdType").eq(type)
    queryIndex("Idx_" + nameRangeKey(), nameRangeKey(), valueRangeKey(), rkc)
  }
}
