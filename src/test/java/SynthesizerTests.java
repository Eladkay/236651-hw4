import il.ac.technion.cs.matrices.ast.AstNode;
import il.ac.technion.cs.matrices.matrix.ConcreteMatrix;
import il.ac.technion.cs.matrices.matrix.IMatrix;
import il.ac.technion.cs.matrices.synthesis.MySynthesizer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class SynthesizerTests {
    public static void main(String[] args) {
        MySynthesizer synthesizer = new MySynthesizer();
        System.out.println(synthesizer.enumerate(2, 1).size());
    }

    @Test
    public void testEnumerator() {
        MySynthesizer synthesizer = new MySynthesizer();
        assert synthesizer.enumerate(0, 1).size() == 1;
        // TODO You should add more tests here
    }

    @Test
    public void testSynthesisDepth2() {
        MySynthesizer synthesizer = new MySynthesizer();
        IMatrix<Double> mat1 = ConcreteMatrix.identity(3);
        IMatrix<Double> mat2 = new ConcreteMatrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        IMatrix<Double> mat3 = new ConcreteMatrix(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}});
        IMatrix<Double> mat4 = new ConcreteMatrix(new double[][]{{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}});

        // example 1 - depth 0: synthesizes Variable(0)
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples1 = Map.of(
                List.of(mat2), mat2,
                List.of(mat1), mat1
        );
        AstNode<? extends IMatrix<?>> result1 = synthesizer.synthesize(examples1);
        assert result1.evaluate(mat1).equals(mat1);
        assert result1.evaluate(mat2).equals(mat2);

        // example 2 - depth 1: synthesizes Call("add", Variable(0), Variable(1))
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples2 = Map.of(
                List.of(mat1, mat2), mat1.add(mat2)
        );
        AstNode<? extends IMatrix<?>> result2 = synthesizer.synthesize(examples2);
        assert result2.evaluate(mat1, mat2).equals(mat1.add(mat2));

        // example 3 - depth 1: synthesizes Call("multiply", Variable(0), Variable(1))
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples3 = Map.of(
                List.of(mat3, mat4), mat3.multiply(mat4) // not of the same size
        );
        AstNode<? extends IMatrix<?>> result3 = synthesizer.synthesize(examples3);
        assert result3.evaluate(mat3, mat4).equals(mat3.multiply(mat4));

        // example 4 - depth 2: synthesizes Call("add", Call("add", Variable(0), Variable(1)), Variable(2)) or
        //                      Call("multiply", Variable(0), Constant(3)) if you can get a 3
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples4 = Map.of(
                List.of(mat1, mat1, mat1), mat1.multiply(3.0) // not all distinct
        );
        AstNode<? extends IMatrix<?>> result4 = synthesizer.synthesize(examples4);
        assert result4.evaluate(mat1, mat2, mat3).equals(mat1.add(mat2).add(mat3));
    }

    @Test
    public void testSynthesisDepth3() {
        MySynthesizer synthesizer = new MySynthesizer();
        IMatrix<Double> mat1 = ConcreteMatrix.identity(3);
        IMatrix<Double> mat2 = new ConcreteMatrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        IMatrix<Double> mat3 = new ConcreteMatrix(new double[][]{{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}});
        IMatrix<Double> mat4 = new ConcreteMatrix(new double[][]{{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}});

        // example 1 - depth 3: synthesizes Call("multiply", Variable(3), Variable(2)).add(Variable(1)).add(Variable(0))
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples1 = Map.of(
                List.of(mat1, mat1, mat1, mat1), mat1.multiply(4.0)
        );
        AstNode<? extends IMatrix<?>> result1 = synthesizer.synthesize(examples1);
        assert result1.evaluate(mat1, mat1, mat1, mat1).equals(mat1.multiply(4.0));

        // example 2 - depth 3: synthesizes Call("add", Call("add", Call("multiply", Variable(3), Variable(2)), Variable(1)), Variable(0))
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples2 = Map.of(
                List.of(mat1, mat2, mat3, mat4), mat4.multiply(mat3).add(mat2).add(mat1)
        );
        AstNode<? extends IMatrix<?>> result2 = synthesizer.synthesize(examples2);
        assert result2.evaluate(mat1, mat2, mat3, mat4).equals(mat4.multiply(mat3).add(mat2).add(mat1));
    }

    // This is a reach goal for the exercise
    @Test
    public void testSynthesisWithSmt() {
        MySynthesizer synthesizer = new MySynthesizer();
        IMatrix<Double> mat1 = ConcreteMatrix.identity(3);
        IMatrix<Double> mat2 = new ConcreteMatrix(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
        // example: synthesizes Call("add", Variable(0), Constant({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}))
        Map<List<IMatrix<Double>>, IMatrix<Double>> examples = Map.of(
                List.of(mat1), mat1.add(mat2),
                List.of(mat2), mat2.add(mat2),
                List.of(ConcreteMatrix.zeros(3, 3)), mat2
        );
        AstNode<? extends IMatrix<?>> result = synthesizer.synthesize(examples);
        assert result.evaluate(mat1).equals(mat1.add(mat2));
        assert result.evaluate(mat2).equals(mat2.add(mat2));
        assert result.evaluate(ConcreteMatrix.zeros(3, 3)).equals(mat2);
    }
}
