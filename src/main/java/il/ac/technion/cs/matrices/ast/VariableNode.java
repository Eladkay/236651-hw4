package il.ac.technion.cs.matrices.ast;

/**
 * A node representing an input variable in the program.
 * Input variables are zero-indexed and are indexed consistently across the entire program.
 * That is, if a variable is indexed zero in one part of the program, it is the same variable
 * all throughout the program.
 * <p>
 * Index zero is guaranteed to be always defined - that is, every program is guaranteed
 * to be evaluated with at least one variable.
 */
public class VariableNode<T> implements AstNode<T> {
    private final int index;

    /**
     * Creates a new variable node.
     *
     * @param index The index of the variable
     */
    public VariableNode(int index) {
        this.index = index;
    }

    @SafeVarargs
    @Override
    public final T evaluate(Object... variables) {
        return (T) variables[index];
    }

    @Override
    public String toString() {
        return "var" + index;
    }
}
