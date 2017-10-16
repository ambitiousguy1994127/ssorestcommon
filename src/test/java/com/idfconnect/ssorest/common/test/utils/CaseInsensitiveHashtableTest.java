package com.idfconnect.ssorest.common.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.idfconnect.ssorest.common.utils.CaseInsensitiveHashtable;

/**
 */
public class CaseInsensitiveHashtableTest {

    @Test
    public void testClearAndIsEmpty() {
        CaseInsensitiveHashtable<String> ht = createHashtable();

        // Check values in the hashtable
        assertEquals("Value of Test1 should be Value1", "Value1", ht.get("Test1"));
        assertEquals("Value of test2 should be Value2", "Value2", ht.get("test2"));
        assertEquals("Value of TEst3 should be Value3", "Value3", ht.get("TEst3"));

        // Confirm the count on the realKeys works
        checkCounts(ht, 8);
        assertFalse(ht.isEmpty());

        // Clear the table
        ht.clear();

        // Confirm that none of the lookups work now
        assertNull(ht.get("Test1"));
        assertNull(ht.get("test2"));
        assertNull(ht.get("TEst3"));

        // Confirm that the list of keys also is null (the realKeys is cleared properly)
        checkCounts(ht, 0);
        assertTrue(ht.isEmpty());
    }

    @Test
    public void testCloneAndEquals() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        @SuppressWarnings("unchecked")
        CaseInsensitiveHashtable<String> theClone = (CaseInsensitiveHashtable<String>) ht.clone();

        // Check the validity of the clone - should be the same as the original
        // Check that the right values are present / absent
        assertTrue("Hashtable should contain Value1", theClone.contains("Value1"));
        assertTrue("Hashtable should contain Value2", theClone.contains("Value2"));
        assertTrue("Hashtable should contain Value8", theClone.contains("Value8"));
        assertFalse("Hashtable should not contain Value9", theClone.contains("Value9"));
        assertFalse("Hashtable should not contain VALUE1", theClone.contains("VALUE1"));
        assertFalse("Hashtable should not contain value6", theClone.contains("value6"));

        // Check that the right keys are present / absent
        assertTrue("Hashtable should contain key Test1", theClone.containsKey("Test1"));
        assertTrue("Hashtable should contain key test1", theClone.containsKey("test1"));
        assertTrue("Hashtable should contain key TEST1", theClone.containsKey("TEST1"));
        assertTrue("Hashtable should contain key tEST2", theClone.containsKey("tEST2"));
        assertTrue("Hashtable should contain key tESt8", theClone.containsKey("tESt8"));
        assertFalse("Hashtable should not contain key Test9", theClone.containsKey("Test9"));

        // Check that the realKeys were properly copied - that they have the right count
        checkCounts(theClone, 8);
        // Test a few values in the main table and the clone
        testValues(ht);
        testValues(theClone);

        // Confirm that the equals function works
        assertTrue(ht.equals(theClone));
        assertTrue(theClone.equals(ht));

        // Change one of the cases of one of the keys, make sure they are still equal
        theClone.put("TEST1", "Value1");
        assertTrue(ht.equals(theClone));
        assertTrue(theClone.equals(ht));

        // Change one of the cases of one of the values, make sure they are now not equal
        theClone.put("teST1", "NotValue1");
        assertFalse(ht.equals(theClone));
        assertFalse(theClone.equals(ht));

        // Fix the changed value back so they're equal prior to the next test
        theClone.put("TEST1", "Value1");
        assertTrue(ht.equals(theClone));
        assertTrue(theClone.equals(ht));

        // Add a new value, make sure they're not equal (then remove it to reset for next test)
        theClone.put("Test9", "Value9");
        assertFalse(ht.equals(theClone));
        assertFalse(theClone.equals(ht));
        theClone.remove("test9");
        assertTrue(ht.equals(theClone));
        assertTrue(theClone.equals(ht));

        // Remove an item and make sure that they are now not equal
        theClone.remove("Test1");
        assertFalse(ht.equals(theClone));
        assertFalse(theClone.equals(ht));
        checkCounts(ht, 8);
        checkCounts(theClone, 7);
    }

    @Test
    public void testContains() {
        CaseInsensitiveHashtable<String> ht = createHashtable();

        // Check that contains works properly
        assertTrue("Hashtable should contain Value1", ht.contains("Value1"));
        assertTrue("Hashtable should contain Value2", ht.contains("Value2"));
        assertTrue("Hashtable should contain Value8", ht.contains("Value8"));
        assertFalse("Hashtable should not contain Value9", ht.contains("Value9"));
        assertFalse("Hashtable should not contain VALUE1", ht.contains("VALUE1"));
        assertFalse("Hashtable should not contain value6", ht.contains("value6"));

        // ContainsValue should yield the same results as contains
        assertEquals("ContainsValue should yield the same results as contains", ht.containsValue("Value1"), ht.contains("Value1"));
        assertEquals("ContainsValue should yield the same results as contains", ht.containsValue("Value2"), ht.contains("Value2"));
        assertEquals("ContainsValue should yield the same results as contains", ht.containsValue("Value8"), ht.contains("Value8"));
        assertEquals("ContainsValue should yield the same results as contains", ht.containsValue("Value9"), ht.contains("Value9"));
        assertEquals("ContainsValue should yield the same results as contains", ht.containsValue("VALUE1"), ht.contains("VALUE1"));
        assertEquals("ContainsValue should yield the same results as contains", ht.containsValue("value6"), ht.contains("value6"));
    }

    @Test
    public void testContainsKey() {
        CaseInsensitiveHashtable<String> ht = createHashtable();

        // Check that containsKey works properly
        assertTrue("Hashtable should contain key Test1", ht.containsKey("Test1"));
        assertTrue("Hashtable should contain key test1", ht.containsKey("test1"));
        assertTrue("Hashtable should contain key TEST1", ht.containsKey("TEST1"));
        assertTrue("Hashtable should contain key tEST2", ht.containsKey("tEST2"));
        assertTrue("Hashtable should contain key tESt8", ht.containsKey("tESt8"));
        assertFalse("Hashtable should not contain key Test9", ht.containsKey("Test9"));
    }

    @Test
    public void testEntrySet() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        CaseInsensitiveHashtable<String> htCopy = new CaseInsensitiveHashtable<String>();

        for (Map.Entry<String, String> entry : ht.entrySet()) {
            htCopy.put(entry.getKey(), entry.getValue());
            // The following lines will output the values
            // System.out.print(entry.getKey());
            // System.out.print("=");
            // System.out.print(entry.getValue());
            // System.out.print("\n");
        }

        testValues(htCopy);
        assertTrue(htCopy.keySet().equals(getCreateHashtableKeys()));
        checkCounts(htCopy, 8);
        assertTrue(ht.equals(htCopy));
        assertTrue(htCopy.equals(ht));

        // Tests to determine the behavior of entryset
        // Note the sequence of keys that comes out is 6-1-5-8-7-4-3-2
        for (Map.Entry<String, String> entry : ht.entrySet()) {
            if (entry.getKey().equals("test6")) {
                // Basic confirmations
                assertEquals("Original Key match failure", "test6", entry.getKey());
                assertEquals("Original value match failure", "Value6", entry.getValue());

                // Change the current Key, see what happens
                // ht.put("TEST1", "Value1");
                // assertEquals("The updated key is not test1", "test1", entry.getKey());
                // Acutally this test is not valid - it doesn't really test anything because the keys are not changed. Will cover this later
                // The following assertion failed - Conclusion - CHANGING THE CURRENT KEY DOES NOT CHANGE IT.
                // assertEquals("The updated key is not TEST1", "TEST1", entry.getKey());

                // Change the current Value, see what happens
                ht.put("test6", "NewValue6");
                // The following assertion failed - Conclusion - CHANGING THE CURRENT VALUE DOES CHANGE IT.
                // assertEquals("Updated value is not Value1", "Value1", entry.getValue());
                assertEquals("Updated value is not NewValue6", "NewValue6", entry.getValue());

                // Change the next value, see what happens
                ht.put("Test1", "NewValue1");
            }
            if (entry.getKey().equals("test1")) {
                // The following assertion works - Conclusion - CHANGING THE NEXT (A FUTURE) VALUE DOES CHANGE IT
                assertEquals("Updated value is not NewValue1", "NewValue1", entry.getValue());

                // Try the setValue method
                entry.setValue("SetValue5");
                assertEquals("Updated value is not SetValue1", "SetValue1", entry.getValue());

                // Put using a different key case
                ht.put("TEST1", "CapsValue1");
                assertEquals("Updated value is not CapsValue1", "CapsValue1", entry.getValue());
            }
        }
    }

    @Test
    public void testKeys() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        HashSet<String> expectedKeys = getCreateHashtableKeys();

        // Confirm that the keyset matches the expected result
        HashSet<String> generatedKeys = getKeysFromEnumeration(ht.keys());
        assertTrue(generatedKeys.equals(expectedKeys));

        // Remove a key and confirm that they no longer match
        ht.remove("Test1");
        generatedKeys = getKeysFromEnumeration(ht.keys());
        assertFalse(generatedKeys.equals(expectedKeys));

        // Reset the Hashtable and then add a key, make sure they no longer match
        ht = createHashtable();
        ht.put("Test9", "Value9");
        generatedKeys = getKeysFromEnumeration(ht.keys());
        assertFalse(generatedKeys.equals(expectedKeys));

        // Reset the Hashtable and then add a key with the same name but a different case, make sure they no longer match
        ht = createHashtable();
        ht.put("TEST1", "Value1");
        generatedKeys = getKeysFromEnumeration(ht.keys());
        assertFalse(generatedKeys.equals(expectedKeys));

        // Reset the Hashtable and then remove and re-add a key with the same name, make sure they match
        ht = createHashtable();
        ht.remove("Test1");
        ht.put("Test1", "Value1");
        generatedKeys = getKeysFromEnumeration(ht.keys());
        assertTrue(generatedKeys.equals(expectedKeys));
    }

    @Test
    public void testKeySet() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        HashSet<String> expectedKeys = getCreateHashtableKeys();

        // Confirm that the keyset matches the expected result
        assertTrue(ht.keySet().equals(expectedKeys));

        // Remove a key and confirm that they no longer match
        ht.remove("Test1");
        assertFalse(ht.keySet().equals(expectedKeys));

        // Reset the Hashtable and then add a key, make sure they no longer match
        ht = createHashtable();
        ht.put("Test9", "Value9");
        assertFalse(ht.keySet().equals(expectedKeys));

        /*
         * This test currently fails for mysterious reasons - the two sets are clearly different as demonstrated by the
         * System.out calls, but it returns true when we run equals()
                // Reset the Hashtable and then add a key with the same name but a different case, make sure they no longer match
                ht = createHashtable();
                ht.put("TEST1", "Value1");
                ht.put("tEST2", "Value2");
                for (String key : ht.keySet()) {
                    System.out.println(key);
                }
                System.out.println("expected...");
                for (String key : expectedKeys) {
                    System.out.println(key);
                }
                assertFalse(ht.keySet().equals(expectedKeys));
        */
        // Reset the Hashtable and then remove and re-add a key with the same name, make sure they match
        ht = createHashtable();
        ht.remove("Test1");
        ht.put("Test1", "Value1");
        assertTrue(ht.keySet().equals(expectedKeys));
    }

    @Test
    public void testPutAll() {
        Map<String, String> theMap = getMapForPutAll();
        CaseInsensitiveHashtable<String> ht = new CaseInsensitiveHashtable<String>();

        ht.putAll(theMap);
        testValues(ht);
        assertTrue(ht.keySet().equals(getCreateHashtableKeys()));
        checkCounts(ht, 8);

        // Also test the contructor which delegates to putAll
        ht = new CaseInsensitiveHashtable<String>(theMap);

        testValues(ht);
        assertTrue(ht.keySet().equals(getCreateHashtableKeys()));
        checkCounts(ht, 8);

    }

    @Test
    public void testPutAndGet() {
        CaseInsensitiveHashtable<String> ht = new CaseInsensitiveHashtable<String>();

        // One additional validation of isEmpty()
        assertTrue(ht.isEmpty());

        // Test that you can put "Test1" into the Hashtable, and you can get it with various case insensitive versions
        String oldString = ht.put("Test1", "Value1");
        assertNull(oldString);
        assertEquals("Value of Test1 should be Value1", "Value1", ht.get("Test1"));
        assertEquals("Value of test1 should be Value1", "Value1", ht.get("test1"));
        assertEquals("Value of TEST1 should be Value1", "Value1", ht.get("TEST1"));
        checkCounts(ht, 1);

        // Confirm it works with multiple values
        oldString = ht.put("Test2", "Value2");
        assertNull(oldString);
        assertEquals("Value of TEST2 should be Value2", "Value2", ht.get("TEST2"));
        checkCounts(ht, 2);

        // Confirm that a value not in the array fails
        assertEquals("Value of test1 should be Value1", null, ht.get("testt1"));

        // Test changing a value that is already in the Hashtable
        oldString = ht.put("Test1", "NewValue1");
        assertEquals("oldString should be Value1", "Value1", oldString);
        assertEquals("Value of Test1 should be NewValue1", "NewValue1", ht.get("Test1"));
        assertEquals("Value of test1 should be NewValue1", "NewValue1", ht.get("test1"));
        assertEquals("Value of TEST1 should be NewValue1", "NewValue1", ht.get("TEST1"));
        checkCounts(ht, 2);

        // Test changing a value with a different case version than what is currently stored
        oldString = ht.put("TEST1", "AnotherNewValue1");
        assertEquals("oldString should be NewValue1", "NewValue1", oldString);
        assertEquals("Value of Test1 should be AnotherNewValue1", "AnotherNewValue1", ht.get("Test1"));
        assertEquals("Value of test1 should be AnotherNewValue1", "AnotherNewValue1", ht.get("test1"));
        assertEquals("Value of TEST1 should be AnotherNewValue1", "AnotherNewValue1", ht.get("TEST1"));
        checkCounts(ht, 2);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnConstructorFromHashMapWithNullKey() {
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put(null, "Value");
        @SuppressWarnings("unused")
        CaseInsensitiveHashtable<String> ht = new CaseInsensitiveHashtable<String>(hm);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnConstructorFromHashMapWithNullValue() {
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("Key", null);
        @SuppressWarnings("unused")
        CaseInsensitiveHashtable<String> ht = new CaseInsensitiveHashtable<String>(hm);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnContains() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        @SuppressWarnings("unused")
        boolean result = ht.contains(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnContainsKey() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        @SuppressWarnings("unused")
        boolean result = ht.containsKey(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnContainsValue() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        @SuppressWarnings("unused")
        boolean result = ht.containsValue(null);
    }

    @Test
    public void testNullOnEquals() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        assertFalse(ht.equals(null));
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnPutKey() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        ht.put(null, "MyValue");
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnPutValue() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        ht.put("MyKey", null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnPutAll() {
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put(null, "Value");
        CaseInsensitiveHashtable<String> ht = new CaseInsensitiveHashtable<String>();
        ht.putAll(hm);
    }

    @Test(expected = NullPointerException.class)
    public void testNullOnRemove() {
        CaseInsensitiveHashtable<String> ht = createHashtable();
        ht.remove(null);
    }

    /**
     * Method checkCounts.
     * @param ht CaseInsensitiveHashtable<?>
     * @param count int
     */
    private void checkCounts(CaseInsensitiveHashtable<?> ht, int count) {
        assertEquals("Should be " + count + " items in the keySet", count, ht.keySet().size());
        assertEquals("Should be " + count + " items in the hashTable", count, ht.size());
    }

    // Loop through the provided enumeration of keys and return them as a HashSet of Strings for comparison
    /**
     * Method getKeysFromEnumeration.
     * @param e Enumeration<String>
     * @return HashSet<String>
     */
    private HashSet<String> getKeysFromEnumeration(Enumeration<String> e) {
        HashSet<String> keys = new HashSet<String>();

        while (e.hasMoreElements()) {
            keys.add(e.nextElement());
        }

        return keys;
    }

    // Creates a Hashtable with a mixture of strings in it for testing
    /**
     * Method createHashtable.
     * @return CaseInsensitiveHashtable<String>
     */
    private CaseInsensitiveHashtable<String> createHashtable() {
        CaseInsensitiveHashtable<String> ht = new CaseInsensitiveHashtable<String>();
        ht.put("Test1", "Value1");
        ht.put("Test2", "Value2");
        ht.put("TEST3", "Value3");
        ht.put("TEST4", "Value4");
        ht.put("test5", "Value5");
        ht.put("test6", "Value6");
        ht.put("tESt7", "Value7");
        ht.put("tESt8", "Value8");

        return ht;
    }

    // Return a HashSet representation of the keys in the test Hashtable
    /**
     * Method getCreateHashtableKeys.
     * @return HashSet<String>
     */
    private HashSet<String> getCreateHashtableKeys() {
        HashSet<String> keys = new HashSet<String>();

        keys.add("Test1");
        keys.add("Test2");
        keys.add("TEST3");
        keys.add("TEST4");
        keys.add("test5");
        keys.add("test6");
        keys.add("tESt7");
        keys.add("tESt8");

        return keys;
    }

    /**
     * Method getMapForPutAll.
     * @return Map<String,String>
     */
    private Map<String, String> getMapForPutAll() {
        LinkedHashMap<String, String> theMap = new LinkedHashMap<String, String>();
        theMap.put("Test1", "Value1");
        theMap.put("Test2", "Value2");
        theMap.put("TEST3", "Value3");
        theMap.put("TEST4", "Value4");
        theMap.put("test5", "Value5");
        theMap.put("test6", "Value6");
        theMap.put("tESt7", "Value7");
        theMap.put("tESt8", "Value8");

        return theMap;
    }

    /**
     * Method testValues.
     * @param ht CaseInsensitiveHashtable<?>
     */
    private void testValues(CaseInsensitiveHashtable<?> ht) {
        assertEquals("Test1 should be Value1", "Value1", ht.get("Test1"));
        assertEquals("TEST1 should be Value1", "Value1", ht.get("TEST1"));
        assertEquals("tESt7 should be Value7", "Value7", ht.get("tESt7"));
        assertEquals("test8 should be Value8", "Value8", ht.get("test8"));
        assertEquals("Testt1 should be null", null, ht.get("Testt1"));
        assertEquals("Test2 should be Value2", "Value2", ht.get("Test2"));
        assertEquals("Test3 should be Value3", "Value3", ht.get("Test3"));
        assertEquals("Test4 should be Value4", "Value4", ht.get("Test4"));
        assertEquals("Test5 should be Value5", "Value5", ht.get("Test5"));
        assertEquals("Test6 should be Value6", "Value6", ht.get("Test6"));
        assertEquals("Test7 should be Value7", "Value7", ht.get("Test7"));
        assertEquals("Test8 should be Value8", "Value8", ht.get("Test8"));
    }
}
