package com.google.gson;


import org.osbot.rs07.api.model.Item;

/**
 * Created by Krulvis on 22-Dec-16.
 */
public class JsonRSItem extends JsonElement {

    Item item;

    public JsonRSItem(Item item){
        this.item = item;
    }

    public Item getRSItem(){
        return this.item;
    }



}
