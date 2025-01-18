package il.ac.technion.cs.matrices.ast;

/**
 * A simple class representing a program that takes in a some parameters and returns a single value.
 * The program is represented as an abstract syntax tree (AST) where each node is an expression,
 * and these expressions can be composed.
 * See implementations: {@link ClassConstantNode}, {@link MethodCallNode}, {@link VariableNode}.
 */
public interface AstNode<T> {
    /**
     * Evaluates the program on the given input matrices.
     * @param variables The input matrices. Note that the indexing is global:
     *                  variables are never re-indexed, so even deep in the tree, index zero
     *                  still refers to the same variable.
     *                  It is assumed that this parameter is <emph>never empty</emph>.
     * @return The output matrix
     * @param <T> The entry type of the matrices
     */
    T evaluate(Object... variables);
}
