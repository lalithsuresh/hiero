/*
 * Copyright (c) 2017 VMware Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hillview.utils;

import org.hillview.dataset.LocalDataSet;
import org.hillview.dataset.ParallelDataSet;
import org.hillview.dataset.api.IDataSet;
import org.hillview.table.*;
import org.hillview.table.api.ContentsKind;
import org.hillview.table.api.IColumn;
import org.hillview.table.api.IMembershipSet;
import org.hillview.table.api.ITable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class generates some constant tables for testing purposes.
 */
public class TestTables {
    /**
     * Can be used for testing.
     * @return A small table with some interesting contents.
     */
    public static Table testTable() {
        ColumnDescription c0 = new ColumnDescription("Name", ContentsKind.Category, false);
        ColumnDescription c1 = new ColumnDescription("Age", ContentsKind.Integer, false);
        StringArrayColumn sac = new StringArrayColumn(c0,
                new String[] { "Mike", "John", "Tom", "Bill", "Bill", "Smith", "Donald", "Bruce",
                               "Bob", "Frank", "Richard", "Steve", "Dave" });
        IntArrayColumn iac = new IntArrayColumn(c1, new int[] { 20, 30, 10, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        return new Table(Arrays.asList(sac, iac));
    }

    /**
     * Can be used for testing.
     * @return A small table with some repeated content.
     */
    public static Table testRepTable() {
        ColumnDescription c0 = new ColumnDescription("Name", ContentsKind.Category, false);
        ColumnDescription c1 = new ColumnDescription("Age", ContentsKind.Integer, false);
        StringArrayColumn sac = new StringArrayColumn(c0,
                new String[] { "Mike", "John", "Tom", "Bill", "Bill", "Smith", "Donald", "Bruce",
                        "Bob", "Frank", "Richard", "Steve", "Dave", "Mike", "Ed" });
        IntArrayColumn iac = new IntArrayColumn(c1, new int[] { 20, 30, 10, 10, 20, 30, 20, 30, 10,
                40, 40, 20, 10, 50, 60 });
        return new Table(Arrays.asList(sac, iac));
    }

    /**
     * A table of integers whose rows are typically distinct. Each row is sampled randomly from a
     * domain of size 5^numCols*size. When numCols is small, some collisions are to be expected, but
     * generally the elements are distinct (each row in the range has a probability of 5^{-numCols}
     * of being sampled.)
     * @param size The size of the desired table
     * @param numCols The number of columns
     * @return A table of random integers.
     */
    public static SmallTable getIntTable(final int size, final int numCols) {
        Randomness rn = new Randomness(2); // we want deterministic random numbers for testing
        final List<IColumn> columns = new ArrayList<IColumn>(numCols);
        double exp = 1.0/numCols;
        final int range =  5*((int)Math.pow(size, exp));
        for (int i = 0; i < numCols; i++) {
            final String colName = "Column" + String.valueOf(i);
            columns.add(IntArrayGenerator.getRandIntArray(size, range, colName, rn));
        }
        return new SmallTable(columns);
    }

    /**
     * A table of integers with some missing values. Each column is the just the identity, but with
     * every multiple of some integer mo in {0,..,99} missing.
     * @param size The size of the desired table
     * @param numCols The number of columns
     * @return A table of integers with missing values.
     */
    public static SmallTable getMissingIntTable(final int size, final int numCols) {
        Randomness rn = new Randomness(2); // we want deterministic random numbers for testing
        final List<IColumn> columns = new ArrayList<IColumn>(numCols);
        double exp = 1.0/numCols;
        final int range =  5*((int)Math.pow(size, exp));
        for (int i = 0; i < numCols; i++) {
            int mod = rn.nextInt(9) + 1;
            final String colName = "Missing" + String.valueOf(mod);
            columns.add(IntArrayGenerator.getMissingIntArray(colName, size, mod));
        }
        return new SmallTable(columns);
    }
    /**
     * A table of integers where each row typically occurs multiple times. Each row is sampled
     * randomly from a domain of size size^{4/5}.  Collisions are to be expected, each tuple from
     * the range appears with frequency size^{1/5} in expectation.
     * @param size The size of the desired table
     * @param numCols The number of columns
     * @return A table of integers with repeated rows.
     */
    public static Table getRepIntTable(final int size, final int numCols) {
        Randomness rn = new Randomness(1); // we want deterministic random numbers for testing
        final List<IColumn> columns = new ArrayList<IColumn>(numCols);
        double exp = 0.8 / numCols;
        final int range =  ((int)Math.pow(size, exp));
        for (int i = 0; i < numCols; i++) {
            final String colName = "Column" + String.valueOf(i);
            columns.add(IntArrayGenerator.getRandIntArray(size, range, colName, rn));
        }
        final FullMembership full = new FullMembership(size);
        return new Table(columns, full);
    }

    /**
     * Method generates a table with a specified number of integer columns, where each column is
     * generated by the GetHeavyIntTable Method so the frequencies are geometrically increasing
     * @param numCols number of columns
     * @param size rows per column
     * @param base base parameter for GetHeavyIntTable
     * @param range range parameter for GetHeavyIntTable
     * @return A table of integers.
     */
    public static SmallTable getHeavyIntTable(final int numCols, final int size, final double base,
                                              final int range) {
        Randomness rn = new Randomness(3);
        final List<IColumn> columns = new ArrayList<IColumn>(numCols);
        for (int i = 0; i < numCols; i++) {
            final String colName = "Column" + String.valueOf(i);
            columns.add(IntArrayGenerator.getHeavyIntArray(size, base, range, colName, rn));
        }
        return new SmallTable(columns);
    }


    /**
     * Generates a table with a specified number of correlated columns. Each row has the same
     * absolute value in every column, they only differ in the sign (which is drawn randomly).
     * - Column 0 contains non-negative integers drawn at random from (0, range).
     * - The signs in the i^th column are  controlled by a parameter rho[i] in (0,1) which is
     * drawn at random. The sign of the i^th column is +1 with probability rho[i] and -1 with
     * probability (1 - rho[i]) independently for every row.
     * - The normalized correlation between Column 0 and Column i is 2*rho[i] - 1 in [-1,1], in
     * expectation.
     * @param size The number of rows.
     * @param numCols The number of columns.
     * @param range Each entry lies in {0, ..., range} in absolute value.
     * @return A table with correlated integer columns.
     */
    public static SmallTable getCorrelatedCols(final int size, final int numCols, final int range) {
        Randomness rn = new Randomness(100); // predictable randomness for testing
        double[] rho = new double[numCols];
        ColumnDescription[] desc = new ColumnDescription[numCols];
        String[] name = new String[numCols];
        IntArrayColumn[] intCol = new IntArrayColumn[numCols];
        for (int i =0; i<  numCols; i++) {
            name[i] = "Col" + String.valueOf(i);
            desc[i] = new ColumnDescription(name[i], ContentsKind.Integer, false);
            intCol[i] = new IntArrayColumn(desc[i], size);
            rho[i] = ((i==0) ? 1 : (rho[i-1]*0.8));
            //System.out.printf("Rho %d = %f\n",i, rho[i]);
        }
        for (int i = 0; i < size; i++) {
            int k = rn.nextInt(range);
            for (int j = 0; j < numCols; j++) {
                double x = rn.nextDouble();
                intCol[j].set(i, ((x > rho[j]) ? -k: k));
            }
        }
        final List<IColumn> col = new ArrayList<IColumn>();
        col.addAll(Arrays.asList(intCol).subList(0, numCols));
        return new SmallTable(col);
    }

    /**
     * Splits a Big Table into a list of Small Tables.
     * @param bigTable The big table
     * @param fragmentSize The size of each small Table
     * @return A list of small tables of size at most fragment size.
     */
    public static List<ITable> splitTable(ITable bigTable, int fragmentSize) {
        int tableSize = bigTable.getNumOfRows();
        int numTables = (tableSize / fragmentSize) + 1;
        List<ITable> tableList = new ArrayList<ITable>(numTables);
        int start = 0;
        while (start < tableSize) {
            int thisFragSize = Math.min(fragmentSize, tableSize - start);
            IMembershipSet members = new SparseMembership(start, thisFragSize);
            tableList.add(bigTable.selectRowsFromFullTable(members));
            start += fragmentSize;
        }
        return tableList;
    }

    /**
     * Creates a ParallelDataSet from a Big Table
     * @param bigTable The big table
     * @param fragmentSize The size of each small Table
     * @return A Parallel Data Set containing the data in the Big Table.
     */
    public static ParallelDataSet<ITable> makeParallel(ITable bigTable, int fragmentSize) {
        final List<ITable> tabList = splitTable(bigTable, fragmentSize);
        final ArrayList<IDataSet<ITable>> a = new ArrayList<IDataSet<ITable>>();
        for (ITable t : tabList) {
            LocalDataSet<ITable> ds = new LocalDataSet<ITable>(t);
            a.add(ds);
        }
        return new ParallelDataSet<ITable>(a);
    }
}
