package com.papercraft.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

public class Cart implements Serializable {
    HashMap<Integer, Product> data;

    public Cart() {
        this.data = new HashMap<>();
    }
    public int getTotalQuantity() {
        int total = 0;
        for (Product product : data.values()) {
            total += product.getQuantity();
        }
        return total;
    }

    public Product get(int id){
        return data.get(id);
    }

   //=== PUT=======
    public void put (Product p){
        //check product is existed
        if(data.containsKey(p.getId())){
            data.get(p.getId()).quantityUp(p.getQuantity());
        }
        else{
            data.put(p.getId(),p);
        }
    }
    //==== REMOVE ========
    public boolean remove(int id){
        return data.remove(id) !=null;
    }

    //======== TOTAL =====
    public double total(){
        double sum = 0;
        for(Product p: data.values()){
            double quantity= p.getQuantity();
            double totalPrice= p.getSalePrice() * quantity;
            sum += totalPrice;
        }
        return sum;
    }
    public Collection<Product> getValues() {
        return data.values();
    }
//=== LIST ====
    public Collection<Product> list(){
        return data.values();
    }
    //===== Update ======
    public void update(int id, int quantity) {
        if (data.containsKey(id)) {
            data.get(id).setQuantity(quantity);
        }
    }
}
