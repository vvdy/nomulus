// Copyright 2017 The Nomulus Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package google.registry.model.server;

import static com.google.common.truth.Truth.assertThat;
import static google.registry.model.ofy.ObjectifyService.ofy;
import static google.registry.testing.DatastoreHelper.persistResource;
import static google.registry.testing.JUnitBackports.expectThrows;

import com.google.common.base.Strings;
import google.registry.testing.AppEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class KmsSecretRevisionTest {

  @Rule public final AppEngineRule appEngine = AppEngineRule.builder().withDatastore().build();
  private KmsSecretRevision secretRevision;

  @Before
  public void setUp() {
    secretRevision =
        persistResource(
            new KmsSecretRevision.Builder()
                .setKmsCryptoKeyVersionName("foo")
                .setParent("bar")
                .setEncryptedValue("blah")
                .build());
  }

  @Test
  public void test_setEncryptedValue_tooLong_throwsException() {
    IllegalArgumentException thrown =
        expectThrows(
            IllegalArgumentException.class,
            () ->
                secretRevision =
                    persistResource(
                        new KmsSecretRevision.Builder()
                            .setKmsCryptoKeyVersionName("foo")
                            .setParent("bar")
                            .setEncryptedValue(Strings.repeat("a", 64 * 1024 * 1024 + 1))
                            .build()));
    assertThat(thrown).hasMessageThat().contains("Secret is greater than 67108864 bytes");
  }

  @Test
  public void testPersistence() {
    assertThat(ofy().load().entity(secretRevision).now()).isEqualTo(secretRevision);
  }
}
