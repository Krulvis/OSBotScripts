package api.webapi.actions;

import api.ATMethodProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.osbot.P;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.utility.Condition;

import java.util.ArrayList;

/**
 * Created by Joep on 04-Jul-17.
 */
public class AcceptTrade extends WebAction {

    private String player;
    private Item[] items;
    private boolean hasTraded = false;

    public AcceptTrade(ATMethodProvider api) {
        super(api, "ACCEPTTRADE");
    }


    @Override
    public boolean perform() {
        if (!atTrade.isTradeOpen() && !hasTraded) {
            if (acceptTradeFromTarget()) {
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


    public boolean acceptTradeFromTarget() {
        final RS2Widget[] lines = widgets.get(162, 43).getChildWidgets();
        RS2Widget offerWC = null;
        for (int i = 0; i < lines.length; i++) {
            RS2Widget line = lines[i];
            if (line != null && line.getMessage() != null && !line.getMessage().startsWith("<col=7f007f>")) {
                String txt = line.getMessage();
                String username = txt.replaceAll("\\<[^>]*>", "").replaceAll(" wishes to trade with you.", "").replaceAll("\u00A0", " ").trim();
                if (username.equalsIgnoreCase(player)) {
                    offerWC = line;
                    break;
                }
            }
        }
        return offerWC != null && offerWC.interact();
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
