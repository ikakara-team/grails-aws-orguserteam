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
import com.amazonaws.services.dynamodbv2.document.Item

@Slf4j("LOG")
@CompileStatic
trait TAccountOwned {
  AIdAccount owner
  boolean bload = false

  @DynamoDBAttribute(attributeName = "OwnerId")
  AIdAccount getOwner() {
    if(owner && !bload) {
      bload = owner.load()
    }
    return owner
  }

  boolean isOwnerOrg() {
    return owner instanceof IdOrg
  }

  boolean isOwnerUser() {
    return owner instanceof IdUser
  }

  void setOwner(AIdAccount o) {
    owner = o
  }

  TAccountOwned withOwner(AIdAccount o) {
    owner = o
    return this
  }

  void marshalOwnerIn(Item item) {
    if (item.isPresent("OwnerId")) {
      owner = AIdAccount.toIdAccount(item.getString("OwnerId"))
    }
  }

  Item marshalOwnerOUT(Item outItem, List removeAttributeNull) {
    if (owner != null) {
      outItem = outItem.withString("OwnerId", (String) owner.valueHashKey())
    } else if (removeAttributeNull != null) {
      removeAttributeNull.add("OwnerId")
    }
    return outItem
  }

  boolean ownerEquals(AIdAccount account) {
    // we're going to cheat and not load owner
    return owner ? owner.valueHashKey() == account?.valueHashKey() : false
  }
}
