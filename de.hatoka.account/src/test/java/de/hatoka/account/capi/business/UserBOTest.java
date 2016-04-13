package de.hatoka.account.capi.business;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.hatoka.account.capi.entities.UserPO;
import de.hatoka.test.DerbyEntityManagerRule;

public class UserBOTest
{
    private static UserBO UNDER_TEST;

    @Inject
    private UserBusinessFactory factory;

    @Rule
    public DerbyEntityManagerRule rule = new DerbyEntityManagerRule("AccountTestPU");

    @Before
    public void createTestObject()
    {
        TestBusinessInjectorProvider.get(rule.getModule()).injectMembers(this);
        UNDER_TEST = factory.getUserBO(new UserPO());
    }

    @Test
    public void testCreation()
    {
        assertNotNull("user exists", UNDER_TEST);
    }
}
