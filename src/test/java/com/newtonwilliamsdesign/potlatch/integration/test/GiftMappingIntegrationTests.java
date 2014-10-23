package com.newtonwilliamsdesign.potlatch.integration.test;

import com.newtonwilliamsdesign.potlatch.gift.JPAConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.newtonwilliamsdesign.potlatch.testdata.JPAAssertions.assertTableExists;
import static com.newtonwilliamsdesign.potlatch.testdata.JPAAssertions.assertTableHasColumn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class GiftMappingIntegrationTests {

  @Autowired
  EntityManager manager;

  @Test
  public void thatItemCustomMappingWorks() throws Exception {
    assertTableExists(manager, "GIFTS");
    assertTableExists(manager, "GIFT_USER");

    assertTableHasColumn(manager, "GIFTS", "GIFT_ID");
    assertTableHasColumn(manager, "GIFTS", "SUBMISSION_DATETIME");
  }

}
