package be.kuleuven.gt.stockcheck;


import org.json.JSONException;
import org.json.JSONObject;

public class items {
    String user;
    String type;
    String name;
    int quantity;
    String expdate;
    double purchaseprice;
    double saleprice;

    public items(JSONObject o) throws JSONException {
        try {
            user = o.getString("user");
            type = o.getString("typeamk");
            name = o.getString("name");
            quantity = Integer.parseInt(o.getString("quantity"));
            expdate = o.getString("expdate");
            purchaseprice = o.getDouble("purchaseprice");
            saleprice = o.getDouble("saleprice");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public items(String username,String type, String name, int quantity, String expdate, double purchaseprice, double saleprice) {
        this.user= username;
        this.type = type;
        this.name = name;
        this.quantity = quantity;
        this.expdate = expdate;
        this.purchaseprice = purchaseprice;
        this.saleprice = saleprice;
    }


    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExp_date() {
        return expdate;
    }

    public double getPurchase_price() {
        return purchaseprice;
    }

    public double getSale_price() {
        return saleprice;
    }
}





