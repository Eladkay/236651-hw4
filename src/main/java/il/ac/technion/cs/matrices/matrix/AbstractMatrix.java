package il.ac.technion.cs.matrices.matrix;

import io.github.cvc5.Kind;
import io.github.cvc5.Solver;
import io.github.cvc5.Sort;
import io.github.cvc5.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an implementation of a matrix
 * with term-valued entries. It supports vectors represented
 * as n x 1 or 1 x n matrices
 * The matrix is 0-indexed and its entries are
 * stored in a row-major order.
 * Objects of this class are expected to be immutable.
 *
 * @author kinsbruner
 * @version 1.0
 */
public final class AbstractMatrix implements IMatrix<Term> {
    private final int rows;
    private final int columns;
    private final Term[][] data;
    public static final Solver solver = new Solver();

    /**
     * Constructs a new matrix with the given dimensions
     * and initializes all entries to constant zero.
     *
     * @param rows    The number of rows
     * @param columns The number of columns
     * @throws IllegalArgumentException If the number of rows or columns is non-positive
     */
    public AbstractMatrix(int rows, int columns) {
        if (rows <= 0) {
            throw new IllegalArgumentException("The number of rows must be positive");
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("The number of columns must be positive");
        }
        this.rows = rows;
        this.columns = columns;
        this.data = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = solver.mkReal(0);
            }
        }
    }

    /**
     * Constructs a new matrix with the given entries.
     * Note that this operation <b>copies the given matrix</b>.
     *
     * @param data The entries of the matrix
     * @throws IllegalArgumentException If the matrix is empty or not rectangular
     */
    public AbstractMatrix(Term[][] data) {
        if (data.length == 0) {
            throw new IllegalArgumentException("The matrix must have at least one row");
        }
        int numCols = data[0].length;
        if (numCols == 0) {
            throw new IllegalArgumentException("The matrix must have at least one column");
        }
        for (int i = 1; i < data.length; i++) {
            if (data[i].length != numCols) {
                throw new IllegalArgumentException("All rows must have the same number of columns");
            }
        }
        this.rows = data.length;
        this.columns = data[0].length;
        // deep clone
        this.data = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, columns);
        }
    }

    /**
     * Constructs an identity matrix of the given size.
     *
     * @param size The size of the matrix
     * @return The new identity matrix
     * @throws IllegalArgumentException If the size is non-positive
     */
    public static AbstractMatrix identity(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be positive");
        }
        AbstractMatrix ret = new AbstractMatrix(size, size);
        for (int i = 0; i < size; i++) {
            ret.data[i][i] = solver.mkReal(1);
        }
        return ret;
    }

    @Override
    public AbstractMatrix identityLike() {
        if (getRows() != getColumns()) {
            throw new UnsupportedOperationException("The matrix must be square");
        }
        return identity(getRows());
    }

    /**
     * Constructs a matrix of the given size with all entries set to zero.
     *
     * @param rows    The number of rows
     * @param columns The number of columns
     * @return The new zero matrix
     * @throws IllegalArgumentException If the number of rows or columns is non-positive
     */
    public static AbstractMatrix zeros(int rows, int columns) {
        return new AbstractMatrix(rows, columns);
    }

    @Override
    public AbstractMatrix zerosLike() {
        return zeros(getRows(), getColumns());
    }

    /**
     * Constructs a new matrix with the given dimensions and fresh variable entries.
     *
     * @param rows    The number of rows
     * @param columns The number of columns
     * @param tag     A tag to add to the variable names
     * @return The new matrix
     */
    public static AbstractMatrix fresh(int rows, int columns, String tag) {
        Term[][] data = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = solver.declareFun("x" + i + "_" + j + "_" + tag, new Sort[]{}, solver.getRealSort());
            }
        }
        return new AbstractMatrix(data);
    }

    /**
     * Constructs a new matrix with fresh variable entries with the same dimensions as this matrix
     *
     * @param tag A tag to add to the variable names
     * @return The new matrix
     */
    public AbstractMatrix freshLike(String tag) {
        return fresh(getRows(), getColumns(), tag);
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return columns;
    }

    @Override
    public Term get(int row, int column) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("The row index is out of bounds");
        }
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("The column index is out of bounds");
        }
        return data[row][column];
    }

    @Override
    public AbstractMatrix exceptAt(int row, int column, Term value) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("The row index is out of bounds");
        }
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("The column index is out of bounds");
        }
        Term[][] newData = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = i == row && j == column ? value : data[i][j];
            }
        }
        return new AbstractMatrix(newData);
    }


    @Override
    public AbstractMatrix dropRow(int row) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("The row index is out of bounds");
        }
        Term[][] newData = new Term[rows - 1][columns];
        for (int i = 0; i < rows; i++) {
            if (i == row) {
                continue;
            }
            System.arraycopy(data[i], 0, newData[i < row ? i : i - 1], 0, columns);
        }
        return new AbstractMatrix(newData);
    }


    @Override
    public AbstractMatrix dropColumn(int column) {
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("The column index is out of bounds");
        }
        Term[][] newData = new Term[rows][columns - 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (j == column) {
                    continue;
                }
                newData[i][j < column ? j : j - 1] = data[i][j];
            }
        }
        return new AbstractMatrix(newData);
    }


    @Override
    public AbstractMatrix transpose() {
        Term[][] newData = new Term[columns][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[j][i] = data[i][j];
            }
        }
        return new AbstractMatrix(newData);
    }


    @Override
    public AbstractMatrix multiply(IMatrix<Term> other) {
        if (columns != other.getRows()) {
            throw new IllegalArgumentException("The number of columns of the first matrix must be equal to the number of rows of the second matrix");
        }
        Term[][] newData = new Term[rows][other.getColumns()];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.getColumns(); j++) {
                Term sum = solver.mkReal(0);
                for (int k = 0; k < columns; k++) {
                    sum = solver.mkTerm(Kind.ADD, sum,
                            solver.mkTerm(Kind.MULT, data[i][k], other.get(k, j)));
                }
                newData[i][j] = sum;
            }
        }
        return new AbstractMatrix(newData);
    }


    @Override
    public AbstractMatrix multiply(Term scalar) {
        Term[][] newData = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = solver.mkTerm(Kind.MULT, data[i][j], scalar);
            }
        }
        return new AbstractMatrix(newData);
    }

    @Override
    public AbstractMatrix add(IMatrix<Term> other) {
        if (rows != other.getRows() || columns != other.getColumns()) {
            throw new IllegalArgumentException("The matrices must have the same dimensions");
        }
        Term[][] newData = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = solver.mkTerm(Kind.ADD, data[i][j], other.get(i, j));
            }
        }
        return new AbstractMatrix(newData);
    }

    @Override
    public AbstractMatrix negate() {
        Term[][] newData = new Term[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = solver.mkTerm(Kind.ADD, data[i][j]);
            }
        }
        return new AbstractMatrix(newData);
    }

    @Override
    public AbstractMatrix resize(int newRows, int newColumns) {
        if (newRows * newColumns != rows * columns) {
            throw new IllegalArgumentException("The number of entries must remain the same");
        }
        Term[][] newData = new Term[newRows][newColumns];
        Term[] entries = new Term[rows * columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, entries, i * columns, columns);
        }
        for (int i = 0; i < newRows; i++) {
            System.arraycopy(entries, i * newColumns, newData[i], 0, newColumns);
        }
        return new AbstractMatrix(newData);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractMatrix other)) {
            return false;
        }
        if (rows != other.rows || columns != other.columns) {
            return false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (!data[i][j].equals(other.data[i][j])) {
                    return false;
                }
            }
            // Can't use `java.util.Arrays.equals(data[i], other.data[i])`
            // because it distinguishes zero with negative zero
        }
        return true;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("hashCode() is not supported");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < rows; i++) {
            if (i > 0) {
                sb.append(", \n");
            }
            for (int j = 0; j < columns; j++) {
                if (j > 0) {
                    sb.append(", ");
                }
                sb.append(data[i][j]);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public List<Term> equate(AbstractMatrix other) {
        if (rows != other.rows || columns != other.columns) {
            throw new IllegalArgumentException("The matrices must have the same dimensions");
        }
        List<Term> constraints = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                constraints.add(solver.mkTerm(Kind.EQUAL, data[i][j], other.data[i][j]));
            }
        }
        return constraints;
    }
}
