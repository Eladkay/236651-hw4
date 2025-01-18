import il.ac.technion.cs.matrices.matrix.ConcreteMatrix;
import il.ac.technion.cs.matrices.matrix.IMatrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConcreteMatrixTests {
    @Test
    public void emptyConstructor_exceptAt() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        assert matrix.get(0, 0) == 1;
        assert matrix.get(0, 1) == 2;
        assert matrix.get(1, 0) == 3;
        assert matrix.get(1, 1) == 4;
    }

    @Test
    public void zeroIsEmptyConstructor() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        ConcreteMatrix zero = ConcreteMatrix.zeros(2, 3);
        assert matrix.equals(zero);
    }

    @Test
    public void identityIsEmptyConstructor() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(1, 1, 1.0);
        ConcreteMatrix identity = ConcreteMatrix.identity(2);
        assert matrix.equals(identity);
    }

    @Test
    public void arrayConstructorCopies() {
        double[][] array = {{1, 2}, {3, 4}};
        ConcreteMatrix matrix = new ConcreteMatrix(array);
        array[0][0] = 0;
        assert matrix.get(0, 1) == 2;
    }

    @Test
    public void emptyConstructor_getRows_getCols() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        assert matrix.getRows() == 2;
        assert matrix.getColumns() == 3;
    }

    @Test
    public void toStringTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        assert matrix.toString().equals("[1.0, 2.0, 0.0, \n3.0, 4.0, 0.0]");
    }

    @Test
    public void dropRowColTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        ConcreteMatrix matrix2 = matrix.dropRow(1);
        assert matrix2.getColumns() == 3;
        assert matrix2.getRows() == 1;
        assert matrix2.get(0, 0) == 1;
        assert matrix2.get(0, 1) == 2;
        assert matrix2.get(0, 2) == 0;

        matrix = matrix.dropRow(0);
        assert matrix.getColumns() == 3;
        assert matrix.getRows() == 1;
        assert matrix.get(0, 0) == 3;
        assert matrix.get(0, 1) == 4;
        assert matrix.get(0, 2) == 0;

        matrix = matrix.dropColumn(1);
        assert matrix.getColumns() == 2;
        assert matrix.getRows() == 1;
        assert matrix.get(0, 0) == 3;
        assert matrix.get(0, 1) == 0;
    }

    @Test
    public void everythingThrowsWhatItShould() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.get(2, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.get(0, 3));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.get(-2, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.get(0, -3));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.exceptAt(2, 0, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.exceptAt(1, 3, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.exceptAt(-2, 0, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.exceptAt(0, -3, 0.0));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.dropRow(2));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.dropRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.dropColumn(3));
        assertThrows(IndexOutOfBoundsException.class, () -> matrix.dropColumn(-1));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteMatrix(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteMatrix(3, 0));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteMatrix(new double[][]{}));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteMatrix(new double[][]{{}}));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteMatrix(new double[][]{{1, 2}, {1}}));
        assertThrows(IllegalArgumentException.class, () -> new ConcreteMatrix(new double[][]{{1, 2}, {1, 2, 3}}));
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.identity(0));
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.identity(-1));
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.zeros(0, 3));
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.zeros(3, 0));
        assertThrows(IllegalArgumentException.class, () -> matrix.add(new ConcreteMatrix(3, 3)));
        assertThrows(IllegalArgumentException.class, () -> matrix.add(new ConcreteMatrix(2, 2)));
        assertThrows(IllegalArgumentException.class, () -> matrix.subtract(new ConcreteMatrix(3, 3)));
        assertThrows(IllegalArgumentException.class, () -> matrix.multiply(new ConcreteMatrix(2, 3)));
        assertThrows(IllegalArgumentException.class, () -> matrix.pow(2));
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.identity(2).pow(-1));
        assertThrows(IllegalArgumentException.class, () -> matrix.resize(3, 4));
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.zeros(2, 2).invert());
        assertThrows(IllegalArgumentException.class, () -> ConcreteMatrix.zeros(2, 3).invert());
        assertThrows(UnsupportedOperationException.class, () -> ConcreteMatrix.zeros(2, 3).identityLike());
    }

    @Test
    public void transposeTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(0, 2, 3.0);
        matrix = matrix.exceptAt(1, 0, 4.0);
        matrix = matrix.exceptAt(1, 1, 5.0);
        matrix = matrix.exceptAt(1, 2, 6.0);
        ConcreteMatrix transposed = matrix.transpose();
        assert transposed.getRows() == 3;
        assert transposed.getColumns() == 2;
        assert transposed.get(0, 0) == 1;
        assert transposed.get(1, 0) == 2;
        assert transposed.get(2, 0) == 3;
        assert transposed.get(0, 1) == 4;
        assert transposed.get(1, 1) == 5;
        assert transposed.get(2, 1) == 6;
    }

    @Test
    public void multiplyTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(0, 2, 3.0);
        matrix = matrix.exceptAt(1, 0, 4.0);
        matrix = matrix.exceptAt(1, 1, 5.0);
        matrix = matrix.exceptAt(1, 2, 6.0);
        ConcreteMatrix other = new ConcreteMatrix(3, 2);
        other = other.exceptAt(0, 0, 7.0);
        other = other.exceptAt(0, 1, 8.0);
        other = other.exceptAt(1, 0, 9.0);
        other = other.exceptAt(1, 1, 10.0);
        other = other.exceptAt(2, 0, 11.0);
        other = other.exceptAt(2, 1, 12.0);
        ConcreteMatrix result = matrix.multiply(other);
        assert result.getRows() == 2;
        assert result.getColumns() == 2;
        assert result.get(0, 0) == 58;
        assert result.get(0, 1) == 64;
        assert result.get(1, 0) == 139;
        assert result.get(1, 1) == 154;
    }

    @Test
    public void addTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(0, 2, 3.0);
        matrix = matrix.exceptAt(1, 0, 4.0);
        matrix = matrix.exceptAt(1, 1, 5.0);
        matrix = matrix.exceptAt(1, 2, 6.0);
        ConcreteMatrix other = new ConcreteMatrix(2, 3);
        other = other.exceptAt(0, 0, 7.0);
        other = other.exceptAt(0, 1, 8.0);
        other = other.exceptAt(0, 2, 9.0);
        other = other.exceptAt(1, 0, 10.0);
        other = other.exceptAt(1, 1, 11.0);
        other = other.exceptAt(1, 2, 12.0);
        ConcreteMatrix result = matrix.add(other);
        assert result.getRows() == 2;
        assert result.getColumns() == 3;
        assert result.get(0, 0) == 8;
        assert result.get(0, 1) == 10;
        assert result.get(0, 2) == 12;
        assert result.get(1, 0) == 14;
        assert result.get(1, 1) == 16;
        assert result.get(1, 2) == 18;
    }

    @Test
    public void arithZero_identity() {
        ConcreteMatrix matrix = new ConcreteMatrix(3, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(0, 2, 3.0);
        matrix = matrix.exceptAt(1, 0, 4.0);
        matrix = matrix.exceptAt(1, 1, 5.0);
        matrix = matrix.exceptAt(1, 2, 6.0);
        matrix = matrix.exceptAt(2, 0, 7.0);
        matrix = matrix.exceptAt(2, 1, 8.0);
        matrix = matrix.exceptAt(2, 2, 9.0);
        ConcreteMatrix zero = ConcreteMatrix.zeros(3, 3);
        ConcreteMatrix identity = ConcreteMatrix.identity(3);
        assert matrix.add(zero).equals(matrix);
        assert matrix.multiply(identity).equals(matrix);
        assert matrix.multiply(zero).equals(zero);
    }

    @Test
    public void negate_subtract_invert() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 3);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(0, 2, 3.0);
        matrix = matrix.exceptAt(1, 0, 4.0);
        matrix = matrix.exceptAt(1, 1, 5.0);
        matrix = matrix.exceptAt(1, 2, 6.0);
        ConcreteMatrix negated = matrix.negate();
        assert negated.getRows() == 2;
        assert negated.getColumns() == 3;
        assert negated.get(0, 0) == -1;
        assert negated.get(0, 1) == -2;
        assert negated.get(0, 2) == -3;
        assert negated.get(1, 0) == -4;
        assert negated.get(1, 1) == -5;
        assert negated.get(1, 2) == -6;
        assert matrix.add(negated).equals(ConcreteMatrix.zeros(2, 3));
        assert ConcreteMatrix.zeros(2, 3).negate().equals(ConcreteMatrix.zeros(2, 3));
        assert ConcreteMatrix.zeros(2, 3).subtract(matrix).equals(matrix.negate());

        matrix = new ConcreteMatrix(2, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        ConcreteMatrix inverted = matrix.invert();
        assert inverted.getRows() == 2;
        assert inverted.getColumns() == 2;
        assert inverted.get(0, 0) == -2;
        assert inverted.get(0, 1) == 1;
        assert inverted.get(1, 0) == 1.5;
        assert inverted.get(1, 1) == -0.5;
        assert matrix.multiply(inverted).equals(ConcreteMatrix.identity(2));
    }

    @Test
    public void resizeTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(4, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        matrix = matrix.exceptAt(2, 0, 5.0);
        matrix = matrix.exceptAt(2, 1, 6.0);
        matrix = matrix.exceptAt(3, 0, 7.0);
        matrix = matrix.exceptAt(3, 1, 8.0);
        ConcreteMatrix resized = matrix.resize(1, 8);
        assert resized.getRows() == 1;
        assert resized.getColumns() == 8;
        assert resized.get(0, 0) == 1;
        assert resized.get(0, 1) == 2;
        assert resized.get(0, 2) == 3;
        assert resized.get(0, 3) == 4;
        assert resized.get(0, 4) == 5;
        assert resized.get(0, 5) == 6;
        assert resized.get(0, 6) == 7;
        assert resized.get(0, 7) == 8;

    }

    @Test
    public void powTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        IMatrix<Double> squared = matrix.pow(2);
        assert squared.getRows() == 2;
        assert squared.getColumns() == 2;
        assert squared.get(0, 0) == 7;
        assert squared.get(0, 1) == 10;
        assert squared.get(1, 0) == 15;
        assert squared.get(1, 1) == 22;
        assert matrix.pow(0).equals(ConcreteMatrix.identity(2));
    }

    @Test
    public void scalarMultiply() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        ConcreteMatrix multiplied = matrix.multiply(2.0);
        assert multiplied.getRows() == 2;
        assert multiplied.getColumns() == 2;
        assert multiplied.get(0, 0) == 2;
        assert multiplied.get(0, 1) == 4;
        assert multiplied.get(1, 0) == 6;
        assert multiplied.get(1, 1) == 8;
    }

    @Test
    public void equalsTest() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 2);
        assert matrix.equals(matrix);
        assert !matrix.equals(null);
        assert !matrix.equals(new Object());
        assert !matrix.equals(new ConcreteMatrix(2, 3));
        assert !matrix.equals(new ConcreteMatrix(3, 2));
        assert !matrix.equals(new ConcreteMatrix(2, 2).exceptAt(0, 0, 1.0));
        assert matrix.hashCode() == matrix.hashCode(); // make sure it doesn't throw an exception and is deterministic
    }

    @Test
    public void identityLike_zerosLike() {
        ConcreteMatrix matrix = new ConcreteMatrix(2, 2);
        matrix = matrix.exceptAt(0, 0, 1.0);
        matrix = matrix.exceptAt(0, 1, 2.0);
        matrix = matrix.exceptAt(1, 0, 3.0);
        matrix = matrix.exceptAt(1, 1, 4.0);
        assert matrix.identityLike().equals(ConcreteMatrix.identity(2));
        assert matrix.zerosLike().equals(ConcreteMatrix.zeros(2, 2));
    }
}
