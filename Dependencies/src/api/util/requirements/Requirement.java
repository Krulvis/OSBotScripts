package api.util.requirements;

import api.ATMethodProvider;

/**
 * Created by s120619 on 25-3-2017.
 */
public abstract class Requirement extends ATMethodProvider{

    public Requirement(ATMethodProvider mp){
        init(mp);
    }

    public abstract boolean hasRequirement();
}
