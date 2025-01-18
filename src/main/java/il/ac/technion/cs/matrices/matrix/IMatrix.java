package il.ac.technion.cs.matrices.matrix;

/**
 * An interface for a matrix of elements of type T.
 * The matrix is immutable, and all operations return new matrices.
 * @param <T> The type of the entries of the matrix
 */
public interface IMatrix<T> {

    /** @return the number of rows of the matrix */
    int getRows();

    /** @return the number of columns of the matrix */
    int getColumns();

    /**
     * Returns the entry at the given row and column.
     * @param row The row index
     * @param col The column index
     * @return The entry at the given row and column
     * @throws IndexOutOfBoundsException If the row or column index is out of bounds
     */
    T get(int row, int col);

    /**
     * Returns a copy of this matrix which is identical for all
     * entries except for the entry at the given row and column,
     * which is set to the given value.
     * @param row The row index
     * @param col The column index
     * @param value The new value of the entry
     * @return The new matrix
     * @throws IndexOutOfBoundsException If the row or column index is out of bounds
     */
    IMatrix<T> exceptAt(int row, int col, T value);

    /**
     * Returns a copy of this matrix with the given row removed.
     * @param row The row index
     * @return The new matrix
     * @throws IndexOutOfBoundsException If the row index is out of bounds
     */
    IMatrix<T> dropRow(int row);

    /**
     * Returns a copy of this matrix with the given column removed.
     * @param col The column index
     * @return The new matrix
     * @throws IndexOutOfBoundsException If the column index is out of bounds
     */
    IMatrix<T> dropColumn(int col);

    /**
     * Returns a copy of this matrix that is transposed.
     * @return The new matrix
     */
    IMatrix<T> transpose();

    /**
     * Returns the product of this matrix (left) and the given matrix (right).
     * @param other The other matrix
     * @return The product of the two matrices
     * @throws IllegalArgumentException If the number of columns of the first matrix is not equal to the number of rows of the second matrix
     */
    IMatrix<T> multiply(IMatrix<T> other);

    /**
     * Returns the product of this matrix and the given scalar.
     * @param scalar The scalar
     * @return The product of the matrix and the scalar
     */
    IMatrix<T> multiply(T scalar);

    /**
     * Returns the sum of this matrix and the given matrix.
     * @param other The other matrix
     * @return The sum of the two matrices
     * @throws IllegalArgumentException If the matrices do not have the same dimensions
     */
    IMatrix<T> add(IMatrix<T> other);

    /**
     * Returns a negated copy of this matrix.
     * @return The negation of the matrix
     */
    IMatrix<T> negate();

    /**
     * Returns the difference of this matrix (left) and the given matrix (right).
     * @param other The other matrix
     * @return The difference of the two matrices
     * @throws IllegalArgumentException If the matrices do not have the same dimensions
     */
    default IMatrix<T> subtract(IMatrix<T> other) {
        return add(other.negate());
    }

    /**
     * Returns a new matrix that is the identity matrix of the same size.
     * @return The identity matrix
     * @throws UnsupportedOperationException If the matrix is not square
     */
    IMatrix<T> identityLike();

    /**
     * Returns a new matrix that is the zero matrix of the same size.
     * @return The zero matrix
     */
    IMatrix<T> zerosLike();

    /**
     * Returns a copy of this matrix, raised to the given exponent.
     * If the exponent is zero, the identity matrix is returned.
     * @param exponent The exponent
     * @return The new matrix
     * @throws IllegalArgumentException If the matrix is not square or the exponent is negative
     */
    default IMatrix<T> pow(int exponent) {
        if (getRows() != getColumns()) {
            throw new IllegalArgumentException("The matrix must be square");
        }
        if (exponent < 0) {
            throw new IllegalArgumentException("The exponent must be non-negative");
        }
        if (exponent == 0) {
            return identityLike();
        }
        IMatrix<T> result = this;
        for (int i = 1; i < exponent; i++) {
            result = result.multiply(this);
        }
        return result;
    }

    /**
     * Returns a copy of this matrix, resized to the given dimensions.
     * The number of entries must remain the same.
     * @param rows The new number of rows
     * @param cols The new number of columns
     * @return The new matrix
     * @throws IllegalArgumentException If the number of entries would change
     */
    IMatrix<T> resize(int rows, int cols);
}
