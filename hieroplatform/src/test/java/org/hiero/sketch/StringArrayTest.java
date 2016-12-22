package org.hiero.sketch;

import org.hiero.sketch.table.ColumnDescription;
import org.hiero.sketch.table.StringArrayColumn;
import org.hiero.sketch.table.api.ContentsKind;
import org.junit.Test;

import static org.junit.Assert.*;

/*
 * Test for StringArrayColumn class.
*/
public class StringArrayTest {
    private final int size = 100;
    private final ColumnDescription desc = new ColumnDescription("test", ContentsKind.String, true);

    /* Test for constructor using length and no arrays*/
    @Test
    public void testStringArrayZero() {
        final StringArrayColumn col = new StringArrayColumn(this.desc, this.size);
        for (int i = 0; i < this.size; i++) {
            if ((i % 5) == 0)
                col.setMissing(i);
            else
                col.set(i, String.valueOf(i));
        }
        assertEquals(col.sizeInRows(), this.size);
        for (int i = 0; i < this.size; i++) {
            if ((i % 5) == 0) {
                assertTrue(col.isMissing(i));
            } else {
                assertFalse(col.isMissing(i));
                assertEquals(String.valueOf(i), col.getString(i));
            }
        }
    }

    /* Test for constructor using data array */
    @Test
    public void testStringArrayOne() {
        final String[] data = new String[this.size];
        for (int i = 0; i < this.size; i++)
            data[i] = String.valueOf(i);
        final StringArrayColumn col = new StringArrayColumn(this.desc, data);
        for (int i = 0; i < this.size; i++)
            if ((i % 5) == 0)
                col.setMissing(i);
        assertEquals(col.sizeInRows(), this.size);
        for (int i = 0; i < this.size; i++) {
            if ((i % 5) == 0) {
                assertTrue(col.isMissing(i));
            } else {
                assertFalse(col.isMissing(i));
                assertEquals(String.valueOf(i), col.getString(i));
            }
        }
    }
}