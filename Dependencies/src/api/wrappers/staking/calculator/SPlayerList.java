package api.wrappers.staking.calculator;

import api.ATMethodProvider;

import java.util.ArrayList;

/**
 * Created by Krulvis on 30-Sep-16.
 */
public class SPlayerList extends ArrayList<SPlayer> {

    public SPlayer getCurrentInterfaceOpponent(ATMethodProvider api){
        return new SPlayer(api.stake.getOtherName1(), api.stake.getOtherSkills());
    }

    public SPlayer get(ATMethodProvider api, String name){
        int i = indexOf(name);
        if(i >= 0){
            return get(i);
        }
        return new SPlayer(name, name.equalsIgnoreCase(api.myPlayer().getName()) ? api.stake.getMySkills() : api.stake.getOtherSkills());
    }

    public boolean contains(String name){
        return indexOf(name) >= 0;
    }

    @Override
    public int indexOf(Object n){
        if(n == null){
            return -1;
        }
        if(n instanceof String){
            for(int i = 0; i < size(); i++){
                if(get(i).name.equalsIgnoreCase((String)n)){
                    return i;
                }
            }
        }
        return -1;
    }
}
