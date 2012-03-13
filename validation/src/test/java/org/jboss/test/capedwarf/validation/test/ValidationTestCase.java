package org.jboss.test.capedwarf.validation.test;

import org.jboss.capedwarf.validation.ValidationHelper;
import org.jboss.test.capedwarf.validation.support.DummyInfo;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Validation test case.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ValidationTestCase {
    @Test
    public void testSmoke() throws Exception {
        Validator validator = ValidationHelper.createValidator();
        validator.validate("foobar");
    }

    @Test
    public void testValidationFail() throws Exception {
        // assertFailValidation(new TopicInfo());
        assertFailValidation(new DummyInfo());

        DummyInfo di1 = new DummyInfo();
        di1.setString("2");
        di1.setBytes(new byte[0]);
        di1.setUsername("alesj");
        assertFailValidation(di1);

        DummyInfo di2 = new DummyInfo();
        di2.setBytes(new byte[]{1, 2});
        di2.setUsername("alesj");
        assertFailValidation(di2);

        DummyInfo di3 = new DummyInfo();
        di3.setString("123");
        di3.setBytes(new byte[]{1, 2});
        di3.setUsername("alesj");
        di3.setEmail("this_is_not_emailXqwert");
        assertFailValidation(di3);

        DummyInfo di4 = new DummyInfo();
        di4.setString("123");
        di4.setBytes(new byte[]{1, 2});
        di4.setUsername("A*");
        assertFailValidation(di4);
    }

    @Test
    public void testValidationPass() throws Exception {
//      TopicInfo ti = new TopicInfo();
//      ti.setName("alesj");
//      assertPassValidation(ti);

        DummyInfo di = new DummyInfo();
        di.setString("123");
        di.setBytes(new byte[]{1, 2});
        assertPassValidation(di);

        DummyInfo di3 = new DummyInfo();
        di3.setString("123");
        di3.setBytes(new byte[]{1, 2});
        di3.setEmail("thisisemail@qwert.com");
        assertPassValidation(di3);

        DummyInfo di4 = new DummyInfo();
        di4.setString("123");
        di4.setBytes(new byte[]{1, 2});
        di4.setEmail("thisisemail@qwert.com");
        di4.setUsername("alesj");
        assertPassValidation(di4);
    }

    protected void assertFailValidation(Object object) {
        Validator validator = ValidationHelper.createValidator();
        Set<ConstraintViolation<Object>> cvs = validator.validate(object);
        Assert.assertNotNull(cvs);
        Assert.assertFalse(cvs.isEmpty());
        ConstraintViolation cv = cvs.iterator().next();
        String msg = cv.getMessage();
        Assert.assertNotNull(msg);
    }

    protected void assertPassValidation(Object object) {
        Validator validator = ValidationHelper.createValidator();
        Set<ConstraintViolation<Object>> cvs = validator.validate(object);
        Assert.assertNotNull(cvs);
        Assert.assertTrue(cvs.isEmpty());
    }
}
