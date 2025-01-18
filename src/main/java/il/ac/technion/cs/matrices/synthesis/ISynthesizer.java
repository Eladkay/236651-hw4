package il.ac.technion.cs.matrices.synthesis;

import il.ac.technion.cs.matrices.matrix.IMatrix;
import il.ac.technion.cs.matrices.ast.AstNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * A synthesizer for matrix expressions.
 */
public interface ISynthesizer {

    /**
     * This method enumerates all possible expressions of a given depth that can be
     * constructed from a given number of variables.
     * @param depth The depth of the expressions. Must be non-negative.
     *              A depth of 0 means that only variables are allowed.
     * @param numVariables The number of variables that can be used in the expressions.
     *                     Must be positive.
     */
    @NotNull
    List<AstNode<? extends IMatrix<?>>> enumerate(int depth, int numVariables);

    /**
     * This method synthesizes a matrix expression that fits the given examples.
     * If it is not possible to synthesize an expression within @{link #TIMEOUT_MILLIS} milliseconds,
     * this method should throw a {@link CannotSynthesizeException}.
     * Note that the returned expression must be able to fit all the examples,
     * and may not be unique. If there are multiple solutions, any one of them is acceptable.
     * @param examples A non-empty, non-null map from input matrices to output matrices.
     *                 The input matrices are the keys, and the output matrices are the values.
     *                 It is guaranteed that this method will always be called with
     *                 all inputs lists having the same length, this length being
     *                 the output program's variable count.
     * @return An expression that fits the examples.
     * @throws CannotSynthesizeException If no expression can be found that fits the examples.
     */
    @NotNull
    AstNode<? extends IMatrix<?>> synthesize(@NotNull Map<List<IMatrix<Double>>, IMatrix<Double>> examples);

    /**
     * The timeout for the synthesis process, in milliseconds.
     */
    long TIMEOUT_MILLIS = 5000;

    /**
     * Returns true if the given start time is less than {@link #TIMEOUT_MILLIS} milliseconds ago.
     * @param startTime The start time
     * @return True if the time has not yet timed out
     */
    static boolean notYetTimeout(long startTime) {
        return System.currentTimeMillis() - startTime < TIMEOUT_MILLIS;
    }
}
