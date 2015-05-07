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

import groovy.transform.ToString
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable
import com.amazonaws.services.dynamodbv2.document.Item
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition
import com.amazonaws.services.dynamodbv2.document.Table
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

import ikakara.awsinstance.aws.AWSInstance
import ikakara.awsinstance.aws.DynamoHelper
import ikakara.awsinstance.dao.ITypeObject
import ikakara.awsinstance.dao.dynamo.ADynamoObject

/**
 * @author Allen
 */
@DynamoDBTable(tableName = "Ids")
@ToString(includePackage=false, includeNames=true, ignoreNulls=true, includeSuperProperties=true)
@Slf4j("LOG")
@CompileStatic
abstract class AIdBase extends AIdObject implements ITypeObject {
  private static String TABLE_NAME

  @DynamoDBAttribute(attributeName = "DisplayName")
  String name
  @DynamoDBAttribute(attributeName = "IdStatus")
  Number status

  protected AIdBase alias

  @Override
  synchronized String tableName() {
    if (!TABLE_NAME) {
      TABLE_NAME = DynamoHelper.getTableName(AIdBase, "grails.plugin.awsorguserteam.dataSource")
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
      Projection PROJECTION_TYPE = new Projection().withProjectionType(ProjectionType.INCLUDE)
      .withNonKeyAttributes("AliasId", "AliasPrefix")
      Projection PROJECTION_ALIAS = new Projection().withProjectionType(ProjectionType.INCLUDE)
      .withNonKeyAttributes("IdType", "CreatedTime")

      CreateTableRequest req = new CreateTableRequest()
      .withTableName(tableName())
      .withAttributeDefinitions(
        new AttributeDefinition(nameHashKey(), ScalarAttributeType.S),
        new AttributeDefinition("AliasId", ScalarAttributeType.S),
        new AttributeDefinition("AliasPrefix", ScalarAttributeType.S),
        new AttributeDefinition("IdType", ScalarAttributeType.S),
        new AttributeDefinition("CreatedTime", ScalarAttributeType.S)
        //new AttributeDefinition("UpdatedTime", ScalarAttributeType.S)
      )
      .withKeySchema(
        new KeySchemaElement(nameHashKey(), KeyType.HASH))
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
          new KeySchemaElement("AliasPrefix", KeyType.RANGE))
        .withProjection(PROJECTION_ALIAS)
        .withProvisionedThroughput(THRUPUT)
      )

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
  void marshalAttributesIN(Item item) {
    super.marshalAttributesIN(item)
    //if (map) {
    String alias_prefix
    String alias_id

    if (item.isPresent("DisplayName")) {
      name = item.getString("DisplayName")
    }
    if (item.isPresent("IdStatus")) {
      status = item.getNumber("IdStatus")
    }
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
  Item marshalItemOUT(List removeAttributeNull) {
    Item outItem = super.marshalItemOUT(removeAttributeNull) ?: new Item()

    if (type) {
      outItem = outItem.withString("IdType", type)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("IdType")
    }
    if (name) {
      outItem = outItem.withString("DisplayName", name)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("DisplayName")
    }
    if (status != null) {
      outItem = outItem.withNumber("IdStatus", status)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("IdStatus")
    }
    if (alias) {
      outItem = outItem.withString("AliasPrefix", alias.typePrefix)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("AliasPrefix")
    }
    if (alias && alias.id != "") {
      outItem = outItem.withString("AliasId", alias.id)
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("AliasId")
    }

    return outItem
  }

  @Override
  void initParameters(Map params) {
    super.initParameters(params)
    //if (params) {
    id = (String) params.id
    name = (String) params.name
    status = (Integer) params.status
    String alias_id = (String) params.alias_id
    String alias_prefix = (String) params.alias_prefix
    alias = toId(alias_prefix + alias_id)
    //}
  }

  AIdBase withId(String id) {
    this.id = id
    return this
  }

  AIdBase withAlias(AIdBase obj) {
    alias = obj
    return this
  }

  @DynamoDBIgnore
  AIdBase getAlias() {
    return alias
  }

  AIdBase isId(String str) {
    if (str?.startsWith(typePrefix)) {
      id = str.substring(typePrefix.length())
      return this
    }
  }

  static AIdBase toId(String id_str) {
    AIdBase obj = new IdUser().isId(id_str)
    if (obj) {
      return obj
    }

    obj = new IdOrg().isId(id_str)
    if (obj) {
      return obj
    }

    obj = new IdFolder().isId(id_str)
    if (obj) {
      return obj
    }

    obj = new IdSlug().isId(id_str)
    if (obj) {
      return obj
    }

    obj = new IdEmail().isId(id_str)
    if (obj) {
      return obj
    }
  }

  @Override
  ADynamoObject newInstance(Item item) {
    ADynamoObject obj

    if (item) {
      // this is hacky to store different configs into one table
      if (item.isPresent("IdType")) {
        String type = item.getString("IdType")
        switch (type) {
        case IdUser.ID_TYPE:  obj = new IdUser();  break
        case IdOrg.ID_TYPE:   obj = new IdOrg();   break
        case IdFolder.ID_TYPE:  obj = new IdFolder();  break
        case IdEmail.ID_TYPE: obj = new IdEmail(); break
        case IdSlug.ID_TYPE:  obj = new IdSlug();  break
        }
      }
    }

    obj?.marshalAttributesIN(item)

    return obj
  }

  AIdBase() {
  }

  AIdBase(Map params) {
    initParameters(params)
  }

  @DynamoDBAttribute(attributeName = "AliasId")
  String getAliasId() {
    alias?.id
  }

  @DynamoDBAttribute(attributeName = "AliasPrefix")
  String getAliasPrefix() {
    alias?.typePrefix
  }

  AIdBase withStatus(int s) {
    status = s
    return this
  }

  List<AIdBase> queryByAlias(String aliasId) {
    queryIndex("Idx_AliasId", "AliasId", aliasId)
  }

  List<AIdBase> queryByType() {
    queryIndex("Idx_IdType", "IdType", type)
  }

  List<AIdBase> queryByAliasAndPrefix() {
    RangeKeyCondition rangeKeyCondition = new RangeKeyCondition("AliasPrefix").eq(aliasPrefix)
    queryIndex("Idx_AliasId", "AliasId", aliasId, rangeKeyCondition)
  }
}
