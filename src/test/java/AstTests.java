import il.ac.technion.cs.matrices.ast.AstNode;
import il.ac.technion.cs.matrices.ast.ClassConstantNode;
import il.ac.technion.cs.matrices.ast.MethodCallNode;
import il.ac.technion.cs.matrices.ast.VariableNode;
// Uncomment this when you're ready to implement the SMT extension:
//import il.ac.technion.cs.matrices.matrix.AbstractMatrix;
import il.ac.technion.cs.matrices.matrix.ConcreteMatrix;
import il.ac.technion.cs.matrices.matrix.IMatrix;
import io.github.cvc5.Term;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AstTests {
    @Test
    public void testClassConstant() {
        ClassConstantNode.ClassValue<IMatrix<?>> value = clazz -> {
            try {
                return (IMatrix<?>) clazz.getMethod("identity", int.class).invoke(null, 3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        ClassConstantNode<IMatrix<?>> node = new ClassConstantNode<>(value);
        IMatrix<Double> result = (IMatrix<Double>) node.evaluate(ConcreteMatrix.identity(1));
        assert result.equals(ConcreteMatrix.identity(3));
//        AbstractMatrix mat = AbstractMatrix.identity(1);
//        IMatrix<Term> result2 = (IMatrix<Term>) node.evaluate(mat);
//        assert result2.equals(AbstractMatrix.identity(3));
    }

    @Test
    public void testVariable() {
        VariableNode<IMatrix<Double>> node = new VariableNode<>(1);
        IMatrix<Double> result = node.evaluate(ConcreteMatrix.identity(2), ConcreteMatrix.identity(1));
        assert result.equals(ConcreteMatrix.identity(1));
    }

    @Test
    public void testMethodCall() {
        MethodCallNode<IMatrix<Double>> node = new MethodCallNode<>("add", new VariableNode<IMatrix<Double>>(0), new VariableNode(1));
        IMatrix<Double> result = node.evaluate(ConcreteMatrix.identity(2), ConcreteMatrix.identity(2));
        assert result.equals(ConcreteMatrix.identity(2).add(ConcreteMatrix.identity(2)));
        assertThrows(RuntimeException.class, node::evaluate);
    }

    @Test
    public void testPolynomial() {
        try {
            ClassConstantNode.ClassValue<IMatrix<?>> identity = ClassConstantNode.getStaticMethodValue("identity", 3);
            AstNode<IMatrix<?>> node = new MethodCallNode<>("add",
                    new MethodCallNode<IMatrix<?>>("multiply", new VariableNode(0), new VariableNode(1)),
                    new ClassConstantNode<>(identity));
            IMatrix<Double> resultConc = (IMatrix<Double>) node.evaluate(ConcreteMatrix.identity(3), ConcreteMatrix.identity(3));
//            IMatrix<Term> resultAbs = (IMatrix<Term>) node.evaluate(AbstractMatrix.identity(3), AbstractMatrix.identity(3));
            // no exception
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
