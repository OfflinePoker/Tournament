package de.hatoka.account.internal.templates.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.hatoka.account.internal.app.models.AccountListModel;
import de.hatoka.account.internal.app.models.AccountVO;
import de.hatoka.common.capi.app.xslt.Processor;
import de.hatoka.common.capi.resource.LocalizationBundle;
import de.hatoka.common.capi.resource.ResourceLocalizer;

public class AccountTemplateTest
{
    private static final String XSLT_STYLESHEET = "account.xslt";
    private static final String RESOURCE_PREFIX = "de/hatoka/account/internal/templates/app/";

    private AccountVO getAccountVO(String id, String email, String name, boolean isActive)
    {
        AccountVO result = new AccountVO();
        result.setActive(isActive);
        result.setId(id);
        result.setOwner(email);
        result.setName(name);
        result.setSelected(isActive);
        return result;
    }

    private Processor getConverter(Locale locale)
    {
        return new Processor(RESOURCE_PREFIX, new ResourceLocalizer(new LocalizationBundle(RESOURCE_PREFIX + "account",
                        locale)));
    }

    private String getResource(String string) throws IOException
    {
        StringWriter writer = new StringWriter();
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(RESOURCE_PREFIX + string);
        if (resourceAsStream == null)
        {
            throw new FileNotFoundException("Can't find resource: " + RESOURCE_PREFIX + string);
        }
        IOUtils.copy(resourceAsStream, writer, "UTF-8");
        return writer.toString();
    }

    @BeforeClass
    public static void initClass()
    {
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Test
    public void testListUsers() throws IOException, SAXException
    {
        AccountListModel model = new AccountListModel();
        model.getAccounts().add(getAccountVO("123456", "test1@test.mail", "Test 1", true));
        model.getAccounts().add(getAccountVO("123457", "test2@test.mail", "Test 2", false));
        StringWriter writer = new StringWriter();
        getConverter(Locale.US).process(model, XSLT_STYLESHEET, writer);
        XMLAssert.assertXMLEqual("account_list en_Us fails", getResource("account_list.result.xml"), writer.toString());
    }

}