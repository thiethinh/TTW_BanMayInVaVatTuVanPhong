package com.papercraft.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class Cart implements Serializable {
    HashMap<Integer, Product> data;

    public Cart() {
        this.data = new HashMap<>();
    }

    public Product get(int id){
        return data.get(id);
    }

    public void put (Product p){
        //check product is existed
        if(data.containsKey(p.getId())){
            data.get(p.getId()).quantityUp(1);
        }
        else{
            data.put(p.getId(),p);
            p.setQuantity(1);
        }
    }

    public boolean remove(int id){
        return data.remove(id) ==null;
    }
    public double total(){
        double sum = 0;
        for(Product p: data.values()){
            sum +=  p.getQuantity() * p.getPrice();
        }
        return sum;
    }

    public Collection<Product> list(){
        return data.values();
    }
}
