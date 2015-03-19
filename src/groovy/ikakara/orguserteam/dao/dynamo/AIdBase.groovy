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

import java.util.Map
import java.util.List

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex
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

import ikakara.awsinstance.aws.AWSInstance
import ikakara.awsinstance.aws.DynamoHelper
import ikakara.awsinstance.dao.ITypeObject
import ikakara.awsinstance.dao.dynamo.ADynamoObject

/**
 *
 * @author Allen
 */
@DynamoDBTable(tableName = "Ids")
@Slf4j("LOG")
@CompileStatic
abstract public class AIdBase extends AIdObject implements ITypeObject {
  private static String TABLE_NAME = null

  protected AIdBase alias

  @Override
  abstract public String getTypePrefix()

  @Override
  abstract public String getType()

  @Override
  synchronized public String tableName() {
    if (TABLE_NAME == null) {
      TABLE_NAME = DynamoHelper.getTableName(AIdBase.class, "grails.plugin.awsorguserteam.dataSource")
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
      Projection PROJECTION_TYPE = new Projection().withProjectionType(ProjectionType.INCLUDE)
      .withNonKeyAttributes("AliasId", "AliasPrefix")
      Projection PROJECTION_ALIAS = new Projection().withProjectionType(ProjectionType.INCLUDE)
      .withNonKeyAttributes("IdType", "AliasPrefix")

      CreateTableRequest req = new CreateTableRequest()
      .withTableName(tableName())
      .withAttributeDefinitions(
        new AttributeDefinition(this.nameHashKey(), ScalarAttributeType.S),
        new AttributeDefinition("AliasId", ScalarAttributeType.S),
        //new AttributeDefinition("AliasPrefix", ScalarAttributeType.S),
        new AttributeDefinition("IdType", ScalarAttributeType.S),
        new AttributeDefinition("CreatedTime", ScalarAttributeType.S)
        //new AttributeDefinition("UpdatedTime", ScalarAttributeType.S)
      )
      .withKeySchema(
        new KeySchemaElement(this.nameHashKey(), KeyType.HASH))
      .withProvisionedThroughput(THRUPUT)
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
        .withIndexName("Idx_AliasId")
        .withKeySchema(
          new KeySchemaElement("AliasId", KeyType.HASH),
          new KeySchemaElement("CreatedTime", KeyType.RANGE))
        .withProjection(PROJECTION_ALIAS)
        .withProvisionedThroughput(THRUPUT)
      )

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
  public void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map != null && !map.isEmpty()) {
    String alias_prefix = null
    String alias_id = null

    if (item.isPresent("AliasPrefix")) {
      alias_prefix = item.getString("AliasPrefix")
    }
    if (item.isPresent("AliasId")) {
      alias_id = item.getString("AliasId")
    }

    alias = toId(alias_prefix + alias_id)

    //}
  }

  @Override
  public Item marshalItemOUT(boolean bRemoveAttributeNull) {
    Item outItem = super.marshalItemOUT(bRemoveAttributeNull)
    if (outItem == null) {
      outItem = new Item()
    }

    if (getType() != null && !"".equals(getType())) {
      outItem = outItem.withString("IdType", getType())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("IdType")
    }

    if (alias != null) {
      outItem = outItem.withString("AliasPrefix", alias.getTypePrefix())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("AliasPrefix")
    }

    if (alias != null && !"".equals(alias.getId())) {
      outItem = outItem.withString("AliasId", alias.getId())
    } else if (bRemoveAttributeNull) {
      outItem = outItem.removeAttribute("AliasId")
    }

    return outItem
  }

  @Override
  public void initParameters(Map params) {
    super.initParameters(params)
    //if (params != null && !params.isEmpty()) {
    id = (String) params.get("id")
    String alias_id = (String) params.get("alias_id")
    String alias_prefix = (String) params.get("alias_prefix")
    alias = toId(alias_prefix + alias_id)
    //}
  }

  public AIdBase withId(String id) {
    this.id = id
    return this
  }

  public AIdBase withAlias(AIdBase obj) {
    alias = obj
    return this
  }

  @DynamoDBIgnore
  public AIdBase getAlias() {
    return alias
  }

  public AIdBase isId(String str) {
    if (str != null && str.startsWith(getTypePrefix())) {
      id = str.substring(getTypePrefix().length())
      return this
    }

    return null
  }

  static public AIdBase toId(String id_str) {
    AIdBase obj = new IdUser().isId(id_str)
    if (obj != null) {
      return obj
    }

    obj = new IdOrg().isId(id_str)
    if (obj != null) {
      return obj
    }

    obj = new IdTeam().isId(id_str)
    if (obj != null) {
      return obj
    }

    obj = new IdSlug().isId(id_str)
    if (obj != null) {
      return obj
    }

    obj = new IdEmail().isId(id_str)
    if (obj != null) {
      return obj
    }

    return null
  }

  @Override
  public ADynamoObject newInstance(Item item) {
    ADynamoObject obj = null

    if (item != null) {
      // this is hacky to store different configs into one table
      if (item.isPresent("IdType")) {
        String type = item.getString("IdType")
        switch (type) {
        case IdUser.ID_TYPE:
          obj = new IdUser()
          obj.marshalAttributesIN(item)
          break
        case IdOrg.ID_TYPE:
          obj = new IdOrg()
          obj.marshalAttributesIN(item)
          break
        case IdTeam.ID_TYPE:
          obj = new IdTeam()
          obj.marshalAttributesIN(item)
          break
        case IdEmail.ID_TYPE:
          obj = new IdEmail()
          obj.marshalAttributesIN(item)
          break
        case IdSlug.ID_TYPE:
          obj = new IdSlug()
          obj.marshalAttributesIN(item)
          break
        }
      }
    }

    return obj
  }

  public AIdBase() {
    super()
  }

  public AIdBase(Map params) {
    super()
    initParameters(params)
  }

  @DynamoDBAttribute(attributeName = "AliasId")
  public String getAliasId() {
    if (alias != null) {
      return alias.getId()
    }
    return null
  }

  @DynamoDBAttribute(attributeName = "AliasPrefix")
  public String getAliasPrefix() {
    if (alias != null) {
      return alias.getTypePrefix()
    }
    return null
  }

  public List<AIdBase> queryByAlias(String aliasId) {
    List list = super.queryIndex("Idx_AliasId", "AliasId", aliasId)
    return list
  }

  public List<AIdBase> queryByType() {
    List list = super.queryIndex("Idx_IdType", "IdType", getType())
    return list
  }

  public List<AIdBase> queryByTypeAndAlias() {
    RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("AliasId").eq(getAliasId())
    List list = super.queryIndex("Idx_IdType", "IdType", getType(), rangeKeyCondition)
    return list
  }

}
