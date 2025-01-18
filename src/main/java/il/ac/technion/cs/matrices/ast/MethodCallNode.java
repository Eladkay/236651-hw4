package il.ac.technion.cs.matrices.ast;

import org.jetbrains.annotations.NotNull;

/**
 * A node representing a method call on a receiver with arguments.
 */
public class MethodCallNode<T> implements AstNode<T> {
    private final @NotNull String method;
    private final @NotNull AstNode<?> receiver;
    private final AstNode<?>[] arguments;

    /**
     * Creates a new method call node.
     * @param method The name of the method to call
     * @param receiver The receiver of the method call, or null if it is a static method
     *                 (but see {@link ClassConstantNode} which can call methods that do not
     *                 necessarily accept matrices as arguments)
     * @param arguments The arguments to pass to the method
     */
    public MethodCallNode(@NotNull String method, @NotNull AstNode<?> receiver, AstNode<?>... arguments) {
        this.method = method;
        this.receiver = receiver;
        this.arguments = arguments;
    }


    @SafeVarargs
    @Override
    public final T evaluate(Object... variables) {
        Object evaluatedReceiver = receiver.evaluate((Object[])variables);
        Object[] evaluatedArguments = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            evaluatedArguments[i] = arguments[i].evaluate((Object[])variables);
        }
        Class<?>[] argumentTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentTypes[i] = evaluatedArguments[i].getClass();
        }
        try {
            return (T) evaluatedReceiver.getClass().getMethod(method, argumentTypes)
                    .invoke(evaluatedReceiver, evaluatedArguments);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(receiver);
        sb.append(".");
        sb.append(method);
        sb.append("(");
        for (int i = 0; i < arguments.length; i++) {
            sb.append(arguments[i]);
            if (i < arguments.length - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();

    }
}
