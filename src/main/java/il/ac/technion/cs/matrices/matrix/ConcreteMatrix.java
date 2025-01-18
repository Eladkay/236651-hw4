package il.ac.technion.cs.matrices.matrix;

/**
 * This class is a basic implementation of a matrix
 * with real-valued entries. It supports vectors represented
 * as n x 1 or 1 x n matrices
 * The matrix is 0-indexed and its entries are
 * stored in a row-major order.
 * Objects of this class are expected to be immutable.
 * @author kinsbruner
 * @version 1.0
 */
public final class ConcreteMatrix implements IMatrix<Double> {
    private final int rows;
    private final int columns;
    private final double[][] data;

    /**
     * Constructs a new matrix with the given dimensions
     * and initializes all entries to zero.
     * @param rows The number of rows
     * @param columns The number of columns
     * @throws IllegalArgumentException If the number of rows or columns is non-positive
     */
    public ConcreteMatrix(int rows, int columns) {
        if (rows <= 0) {
            throw new IllegalArgumentException("The number of rows must be positive");
        }
        if (columns <= 0) {
            throw new IllegalArgumentException("The number of columns must be positive");
        }
        this.rows = rows;
        this.columns = columns;
        this.data = new double[rows][columns];
    }

    /**
     * Constructs a new matrix with the given entries.
     * Note that this operation <b>copies the given matrix</b>.
     * @param data The entries of the matrix
     * @throws IllegalArgumentException If the matrix is empty or not rectangular
     */
    public ConcreteMatrix(double[][] data) {
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
        this.data = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, columns);
        }
    }

    /**
     * Constructs an identity matrix of the given size.
     * @param size The size of the matrix
     * @return The new identity matrix
     * @throws IllegalArgumentException If the size is non-positive
     */
    public static ConcreteMatrix identity(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be positive");
        }
        double[][] data = new double[size][size];
        for (int i = 0; i < size; i++) {
            data[i][i] = 1;
        }
        return new ConcreteMatrix(data);
    }

    /**
     * Constructs a matrix of the given size with all entries set to zero.
     * @param rows The number of rows
     * @param columns The number of columns
     * @return The new zero matrix
     * @throws IllegalArgumentException If the number of rows or columns is non-positive
     */
    public static ConcreteMatrix zeros(int rows, int columns) {
        return new ConcreteMatrix(rows, columns);
    }

    @Override
    public ConcreteMatrix identityLike() {
        if (rows != columns) {
            throw new UnsupportedOperationException("The matrix must be square");
        }
        return identity(rows);
    }

    @Override
    public ConcreteMatrix zerosLike() {
        return new ConcreteMatrix(rows, columns);
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
    public Double get(int row, int column) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("The row index is out of bounds");
        }
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("The column index is out of bounds");
        }
        return data[row][column];
    }

    @Override
    public ConcreteMatrix exceptAt(int row, int column, Double value) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("The row index is out of bounds");
        }
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("The column index is out of bounds");
        }
        double[][] newData = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = i == row && j == column ? value : data[i][j];
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix dropRow(int row) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException("The row index is out of bounds");
        }
        double[][] newData = new double[rows - 1][columns];
        for (int i = 0; i < rows; i++) {
            if (i == row) {
                continue;
            }
            System.arraycopy(data[i], 0, newData[i < row ? i : i - 1], 0, columns);
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix dropColumn(int column) {
        if (column < 0 || column >= columns) {
            throw new IndexOutOfBoundsException("The column index is out of bounds");
        }
        double[][] newData = new double[rows][columns - 1];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (j == column) {
                    continue;
                }
                newData[i][j < column ? j : j - 1] = data[i][j];
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix transpose() {
        double[][] newData = new double[columns][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[j][i] = data[i][j];
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix multiply(IMatrix<Double> other) {
        if (columns != other.getRows()) {
            throw new IllegalArgumentException("The number of columns of the first matrix must be equal to the number of rows of the second matrix");
        }
        double[][] newData = new double[rows][other.getColumns()];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < other.getColumns(); j++) {
                double sum = 0;
                for (int k = 0; k < columns; k++) {
                    sum += data[i][k] * other.get(k, j);
                }
                newData[i][j] = sum;
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix multiply(Double scalar) {
        double[][] newData = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = data[i][j] * scalar;
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix add(IMatrix<Double> other) {
        if (rows != other.getRows() || columns != other.getColumns()) {
            throw new IllegalArgumentException("The matrices must have the same dimensions");
        }
        double[][] newData = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = data[i][j] + other.get(i, j);
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix negate() {
        double[][] newData = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                newData[i][j] = -data[i][j];
            }
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public ConcreteMatrix resize(int newRows, int newColumns) {
        if (newRows * newColumns != rows * columns) {
            throw new IllegalArgumentException("The number of entries must remain the same");
        }
        double[][] newData = new double[newRows][newColumns];
        double[] entries = new double[rows * columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, entries, i * columns, columns);
        }
        for (int i = 0; i < newRows; i++) {
            System.arraycopy(entries, i * newColumns, newData[i], 0, newColumns);
        }
        return new ConcreteMatrix(newData);
    }

    /**
     * Inverts a copy of this matrix.
     * @return The inverse of the matrix
     * @throws IllegalArgumentException If the matrix is not square or if it is non-invertible
     */
    public ConcreteMatrix invert() {
        if (rows != columns) {
            throw new IllegalArgumentException("The matrix must be square");
        }
        ConcreteMatrix augmented = new ConcreteMatrix(rows, columns * 2);
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, augmented.data[i], 0, columns);
            augmented.data[i][columns + i] = 1;
        }
        for (int i = 0; i < rows; i++) {
            double pivot = augmented.data[i][i];
            if (pivot == 0) {
                throw new IllegalArgumentException("The matrix is singular");
            }
            for (int j = 0; j < columns * 2; j++) {
                augmented.data[i][j] /= pivot;
            }
            for (int j = 0; j < rows; j++) {
                if (j == i) {
                    continue;
                }
                double factor = augmented.data[j][i];
                for (int k = 0; k < columns * 2; k++) {
                    augmented.data[j][k] -= factor * augmented.data[i][k];
                }
            }
        }
        double[][] newData = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(augmented.data[i], columns, newData[i], 0, columns);
        }
        return new ConcreteMatrix(newData);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ConcreteMatrix other)) {
            return false;
        }
        if (rows != other.rows || columns != other.columns) {
            return false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (data[i][j] != other.data[i][j]) {
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
        return java.util.Arrays.deepHashCode(data);
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
}
