package api;

/**
 * Created by Krulvis on 29-May-17.
 */
public abstract class ATState<S extends ATScript> extends ATMethodProvider {

    public final S script;
    public String name;

    public ATState(String name, S script) {
        this.name = name;
        this.script = script;
        init(script);
    }

    public ATState(S script) {
        this.script = script;
    }

    public abstract int perform() throws InterruptedException;

    public abstract boolean validate();
}
