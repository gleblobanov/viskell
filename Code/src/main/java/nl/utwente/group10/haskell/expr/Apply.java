package nl.utwente.group10.haskell.expr;

import com.google.common.collect.ImmutableList;
import nl.utwente.group10.haskell.env.Environment;
import nl.utwente.group10.haskell.exceptions.HaskellException;
import nl.utwente.group10.haskell.type.TypeChecker;
import nl.utwente.group10.haskell.type.Type;

import java.util.List;

/**
 * Lazy application of an argument to a function.
 */
public class Apply extends Expression {
    /**
     * The expression to apply the argument to.
     */
    private final Expression func;

    /**
     * The argument to apply.
     */
    private final Expression arg;

    /**
     * Applies an argument to a function to produce a new expression. The application is lazy, the type needs to be
     * analyzed before there is certainty about the validity of this application and the resulting type.
     *
     * @param func The expression to apply argument to.
     * @param arg The argument to apply.
     */
    public Apply(final Expression func, final Expression arg) {
        this.func = func;
        this.arg = arg;
    }

    @Override
    public final Type analyze(final Environment env) throws HaskellException {
        final Type funcType = func.analyze(env);
        final Type argType = arg.analyze(env);
        final Type resType = TypeChecker.makeVariable("b");


        // Rule [App]:
        // IFF  the type of our function is a -> b and the type of our arg is a
        // THEN the type of our result is b
        TypeChecker.unify(this, funcType, Type.fun(argType, resType));

        this.setCachedType(resType);

        return resType;
    }

    @Override
    public final String toHaskell() {
        return String.format("(%s %s)", this.func.toHaskell(), this.arg.toHaskell());
    }

    @Override
    public final String toString() {
        return String.format("(%s %s)", this.func.toString(), this.arg.toString());
    }

    @Override
    public final List<Expression> getChildren() {
        return ImmutableList.of(func, arg);
    }
}
