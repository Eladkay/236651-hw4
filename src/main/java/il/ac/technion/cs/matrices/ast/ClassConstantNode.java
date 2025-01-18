package il.ac.technion.cs.matrices.ast;


/**
 * An expression representing a constant value that is
 * dependent on the class of the first parameter.
 * For example, this can be used to return the identity matrix: we would want
 * to just have a constant in the AST, but it's a different constant
 * depending on the entry type. So, this node allows us to defer the
 * computation of the constant until we know the entry type.
 */
public class ClassConstantNode<T> implements AstNode<T> {
    /**
     * This SAM interface represents the selection of value represented by this node,
     * as a function of the class in use.
     */
    public interface ClassValue<T> {
        /**
         * Returns the value represented by this node, given the class used.
         *
         * @param clazz The class used
         * @return The value represented by this node
         * @throws Exception This method probably uses reflection, so it can throw many different exceptions.
         *                   It is overapproximated here to just throw <code>Exception</code>.
         */
        T getValue(Class<?> clazz) throws Exception;
    }

    /**
     * This method returns a <code>ClassValue</code> that represents a static method call
     * with constant arguments.
     * This is a very common pattern in our code, so we have a helper method for it.
     * For example, to represent the identity matrix, we would call this method with:
     * <code>getStaticMethodValue("identity", 3)</code>
     * At runtime, it would be resolved to <code>ConcreteMatrix.identity(3)</code>,
     * <code>AbstractMatrix.identity(3)</code>, etc.
     * <p>
     *
     * @param methodName The name of the method to call
     * @param args       The arguments to pass to the method (optional)
     * @return A <code>ClassValue</code> that represents the method call
     */
    public static <T> ClassValue<T> getStaticMethodValue(String methodName, Object... args) {
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
            if (argTypes[i].equals(Integer.class)) {
                argTypes[i] = int.class;
            } // hack
        }
        return clazz -> (T) clazz.getMethod(methodName, argTypes).invoke(null, args);
    }

    private final ClassValue<T> value;

    public ClassConstantNode(ClassValue<T> value) {
        this.value = value;
    }

    @SafeVarargs
    @Override
    public final T evaluate(Object... variables) {
        try {
            return value.getValue(variables[0].getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ClassConstant" + value;
    }
}
