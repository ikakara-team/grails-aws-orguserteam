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

import java.util.List
import java.util.Map

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.Projection
import com.amazonaws.services.dynamodbv2.model.ProjectionType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType
import com.amazonaws.services.dynamodbv2.model.TableDescription

import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.document.Item

import ikakara.awsinstance.aws.AWSInstance
import ikakara.awsinstance.aws.DynamoHelper
import ikakara.awsinstance.dao.ITypeObject
import ikakara.awsinstance.dao.dynamo.ADynamoObject
import ikakara.awsinstance.dao.dynamo.ACreatedUpdatedObject

/**
 *
 * @author Allen
 */
@DynamoDBTable(tableName = "MemberGroups")
@Slf4j("LOG")
@CompileStatic
abstract public class AMemberGroupBase extends ACreatedUpdatedObject implements ITypeObject {

  private static String TABLE_NAME = null

  protected AIdBase member // mobile, email, hash
  protected AIdBase group
  protected IdUser  invitedBy
  protected String  member_role
  protected boolean bLoadMember = false
  protected boolean bLoadGroup = false

  @Override
  abstract public String getType()

  @Override
  synchronized public String tableName() {
    if (TABLE_NAME == null) {
      TABLE_NAME = DynamoHelper.getTableName(AMemberGroupBase.class, "grails.plugin.awsorguserteam.dataSource")
      DynamoHelper.initTable(TABLE_NAME, this, "grails.plugin.awsorguserteam.dataSource")
    }
    return TABLE_NAME
  }

  @Override
  public Map initTable() {
    Map map = DynamoHelper.getTableInformation(tableName())
    if (map == null) {
      // Table doesn't exist.  Let's create it.
      ProvisionedThroughput THRUPUT = new ProvisionedThroughput(1L, 1L)
      Projection PROJECTION = new Projection().withProjectionType(ProjectionType.ALL)
      Projection PROJECTION_TYPE = new Projection().withProjectionType(ProjectionType.KEYS_ONLY)

      CreateTableRequest req = new CreateTableRequest()
      .withTableName(tableName())
      .withAttributeDefinitions(
        new AttributeDefinition(this.nameHashKey(), ScalarAttributeType.S),
        new AttributeDefinition(this.nameRangeKey(), ScalarAttributeType.S),
        new AttributeDefinition("IdType", ScalarAttributeType.S),
        new AttributeDefinition("CreatedTime", ScalarAttributeType.S)
      )
      .withKeySchema(
        new KeySchemaElement(this.nameHashKey(), KeyType.HASH),
        new KeySchemaElement(this.nameRangeKey(), KeyType.RANGE))
      .withProvisionedThroughput(THRUPUT)
      .withLocalSecondaryIndexes(
        new LocalSecondaryIndex()
        .withIndexName("Idx_" + this.nameHashKey())
        .withKeySchema(
          new KeySchemaElement(this.nameHashKey(), KeyType.HASH),
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
        .withIndexName("Idx_" + this.nameRangeKey())
        .withKeySchema(
          new KeySchemaElement(this.nameRangeKey(), KeyType.HASH),
          new KeySchemaElement("IdType", KeyType.RANGE))
        .withProjection(PROJECTION)
        .withProvisionedThroughput(THRUPUT))

      Table table = AWSInstance.DYNAMO_DB().createTable(req)

      try {
        // Wait for the table to become active
        TableDescription desc = table.waitForActive()
        map = DynamoHelper.tableDescriptionToMap(desc)
      } catch (Exception ie) {
        LOG.error("initTable" + ie.getMessage())
        map = DynamoHelper.getTableInformation(tableName())
      }

    }
    return map
  }

  @Override
  public Object valueHashKey() {
    if (member != null) {
      return member.valueHashKey()
    }
    return null
  }

  @Override
  public String nameHashKey() {
    return "MemberId"
  }

  @Override
  public Object valueRangeKey() {
    if (group != null) {
      return group.valueHashKey()
    }
    return null
  }

  @Override
  public String nameRangeKey() {
    return "GroupId"
  }

  @Override
  public void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map != null && !map.isEmpty()) {
    if (item.isPresent("MemberId")) {
      setMemberId(item.getString("MemberId"))
    }
    if (item.isPresent("GroupId")) {
      setGroupId(item.getString("GroupId"))
    }
    if (item.isPresent("InvitedById")) {
      setInvitedById(item.getString("InvitedById"))
    }
    if (item.isPresent("MemberRole")) {
      member_role = item.getString("MemberRole")
    }
    //}
  }

  @Override
  public Item marshalItemOUT(boolean bRemoveAttributeNull) {
    Item outItem = super.marshalItemOUT(bRemoveAttributeNull)
    if (outItem == null) {
      outItem = new Item()
    }

    if (member != null) {
      outItem = outItem.withString("MemberId", (String) member.valueHashKey())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("MemberId")
    }

    if (group != null) {
      outItem = outItem.withString("GroupId", (String) group.valueHashKey())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("GroupId")
    }

    if (invitedBy != null) {
      outItem = outItem.withString("InvitedById", (String) invitedBy.valueHashKey())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("InvitedById")
    }

    if (member_role != null && !"".equals(member_role)) {
      outItem = outItem.withString("MemberRole", member_role)
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("MemberRole")
    }

    if (getType() != null && !"".equals(getType())) {
      outItem = outItem.withString("IdType", getType())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("IdType")
    }

    return outItem
  }

  @Override
  public ADynamoObject newInstance(Item item) {
    ADynamoObject obj = null

    if (item != null) {
      // this is hacky to store different configs into one table
      if (item.isPresent("IdType")) {
        String type = item.getString("IdType")
        switch (type) {
        case IdUserOrg.ID_TYPE:
          obj = new IdUserOrg()
          obj.marshalAttributesIN(item)
          break
        case IdUserTeam.ID_TYPE:
          obj = new IdUserTeam()
          obj.marshalAttributesIN(item)
          break
        case IdOrgTeam.ID_TYPE:
          obj = new IdOrgTeam()
          obj.marshalAttributesIN(item)
          break
        case IdEmailOrg.ID_TYPE:
          obj = new IdEmailOrg()
          obj.marshalAttributesIN(item)
          break
        case IdEmailTeam.ID_TYPE:
          obj = new IdEmailTeam()
          obj.marshalAttributesIN(item)
          break
        }
      }
    }

    return obj
  }

  @DynamoDBIgnore
  @Override
  public String getId() {
    return getMemberId() + "_" + getGroupId()
  }

  @Override
  public void setId(String id) {
    if (id != null) {
      String[] ids = id.split("_")
      if (ids.length > 1) {
        setGroupId(ids[1])
        // fix broken contactId
        String str = ids[0]
        if (str.startsWith(" ")) {
          str = str.replaceFirst(" ", "+")
        }
        setMemberId(str)
      }
    }
  }

  public AMemberGroupBase withMember(AIdBase mem) {
    member = mem
    return this
  }

  public AMemberGroupBase withGroup(AIdBase grp) {
    group = grp
    return this
  }

  @DynamoDBIgnore
  public AIdBase getMember() {
    if (member != null && !bLoadMember) {
      bLoadMember = member.load()
    }

    return member
  }

  @DynamoDBIgnore
  public AIdBase getGroup() {
    if (group != null && !bLoadGroup) {
      bLoadGroup = group.load()
    }
    return group
  }

  @Override
  public void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {

    String group_str = (String) params.get("group")
    if (group_str != null) {
      setGroupId(group_str)
    }

    String member_str = (String) params.get("member")
    if (member_str != null) {
      setMemberId(member_str)
    }

    member_role = (String) params.get("member_role")
    //}
  }

  @DynamoDBHashKey(attributeName = "MemberId")
  public String getMemberId() {
    return (String) member.valueHashKey()
  }

  public void setMemberId(String id) {
    member = AIdBase.toId(id)
  }

  @DynamoDBRangeKey(attributeName = "GroupId")
  public String getGroupId() {
    return (String) group.valueHashKey()
  }

  public void setGroupId(String id) {
    group = AIdBase.toId(id)
  }

  @DynamoDBAttribute(attributeName = "InvitedById")
  public String getInvitedById() {
    return (String) invitedBy?.valueHashKey()
  }

  public void setInvitedById(String id) {
    invitedBy = AIdBase.toId(id)
  }

  @DynamoDBAttribute(attributeName = "MemberRole")
  public String getMemberRole() {
    return member_role
  }

  public void setMemberRole(String d) {
    member_role = d
  }

  public AMemberGroupBase() {
    super()
  }

  public AMemberGroupBase(Map params) {
    super()
    initParameters(params)
  }

  public List<AIdBase> _findByType() {
    // Scan items for IdType
    String where = "IdType = :myIdType"
    ValueMap valueMap = new ValueMap()
    .withString(":myIdType", getType())
    List list = super.scan(where, valueMap)

    return list
  }

  public List<AMemberGroupBase> queryByType() {
    List list = super.queryIndex("Idx_IdType", "IdType", getType())
    return list
  }

  public List<AMemberGroupBase> queryByMember() {
    LOG.info("hash:" + nameHashKey() + " value:" + valueHashKey())

    List list = super.query(nameHashKey(), valueHashKey())
    return list
  }

  public List<AMemberGroupBase> queryByMemberAndType() {
    RangeKeyCondition rkc = new RangeKeyCondition("IdType").eq(getType())

    List list = super.queryIndex("Idx_" + this.nameHashKey(), nameHashKey(), valueHashKey(), rkc)
    return list
  }

  public List<AMemberGroupBase> queryByGroup() {
    List list = super.queryIndex("Idx_" + nameRangeKey(), nameRangeKey(), valueRangeKey())
    return list
  }

  public List<AMemberGroupBase> queryByGroupAndType() {
    RangeKeyCondition rkc = new RangeKeyCondition("IdType").eq(getType())

    List list = super.queryIndex("Idx_" + nameRangeKey(), nameRangeKey(), valueRangeKey(), rkc)
    return list
  }
}
