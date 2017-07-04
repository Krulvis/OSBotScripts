package api.webapi.actions;

import api.ATMethodProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.Player;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;

import java.util.ArrayList;

/**
 * Created by Joep on 04-Jul-17.
 */
public class DoTrade extends WebAction {

    private String player;
    private Item[] items;
    private boolean hasTraded = false;

    public DoTrade(ATMethodProvider api) {
        super(api, "DOTRADE");
    }


    @Override
    public boolean perform() {
        if (!atTrade.isTradeOpen() && !hasTraded) {
            if (tradeTarget()) {
                waitFor(2000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return atTrade.isTradeOpen();
                    }
                });
            }
        } else if (atTrade.isFirstTradeOpen()) {
            for (Item i : items) {
                if (i != null && !atTrade.offerExact(i.getId(), i.getAmount())) {
                    return false;
                }
            }
            if (canAcceptFirst() && !atTrade.isFirstTradeAccepted()) {
                if (atTrade.acceptFirstTrade()) {
                    waitFor(2000, new Condition() {
                        @Override
                        public boolean evaluate() {
                            return atTrade.isSecondTradeOpen();
                        }
                    });
                }
            }
        } else if (atTrade.isSecondTradeOpen() && !atTrade.isSecondTradeAccepted()) {
            if (atTrade.acceptSecondTrade()) {
                hasTraded = true;
                waitFor(5000, new Condition() {
                    @Override
                    public boolean evaluate() {
                        return !atTrade.isTradeOpen();
                    }
                });
            }
        } else {
            return true;
        }
        return false;
    }


    public boolean tradeTarget() {
        Player p = getPlayer(player);
        if (p != null && interact(p, "Trade With")) {
            waitFor(5000, new Condition() {
                @Override
                public boolean evaluate() {
                    return atTrade.isTradeOpen();
                }
            });
        }
        return atTrade.isTradeOpen();
    }

    public boolean canAcceptFirst() {
        for (Item i : items) {
            if (atTrade.myAmountOf(i.getId()) != i.getAmount()) {
                return false;
            }
        }
        return true;
    }

    public String getPlayer() {
        return player;
    }

    public Item[] getItems() {
        return items;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    @Override
    public void setActionParameters(JsonObject parameters) {
        if (parameters != null) {
            ArrayList<Item> items = new ArrayList<>();
            String p = parameters.has("player") ? parameters.get("player").getAsString() : null;
            JsonArray toTrade = parameters.has("items") ? parameters.get("items").getAsJsonArray() : null;
            if (toTrade != null) {
                for (int i = 0; i < toTrade.size(); i++) {
                    JsonObject item = toTrade.get(i).getAsJsonObject();
                    int id = item.get("id").getAsInt();
                    int amount = item.get("amount").getAsInt();
                    items.add(new Item(id, amount));
                }
                this.items = items.toArray(new Item[items.size()]);
            }
        }
    }
}
