package be.kuleuven.gt.stockcheck;

public enum Endpoints {

    //Login
    LOGIN_GET_URL("https://studev.groept.be/api/a23PT201/getCredentials"),

    //Signup
    SIGNUP_GET_URL("https://studev.groept.be/api/a23PT201/getCredentials"),
    SIGNUP_POST_URL("https://studev.groept.be/api/a23PT201/setCredentials/"),

    //MainActivity
    MAIN_POST_URL("https://studev.groept.be/api/a23PT201/sendItemDetails/"),
    MAIN_REMOVE_URL("https://studev.groept.be/api/a23PT201/removeItemDetails/"),
    REMOVE_ITEM_URL("https://studev.groept.be/api/a23PT201/removeItem/"),

    //HomeFragment
    HOME_GET_ITEM("https://studev.groept.be/api/a23PT201/getItemInfo/"),
    HOME_GET_CATEGORY_ITEM_COUNTS("https://studev.groept.be/api/a23PT201/getCategoryItemCounts/"),
    CHECK_EXISTING_ITEMS("https://studev.groept.be/api/a23PT201/checkExistingItems/"),
    CHECK_QUANTITY_BELOW_ZERO("https://studev.groept.be/api/a23PT201/checkQuantityBelowZero/"),

    //CategoryFragment
    CATEGORY_GET_ITEMS_BY_CATEGORY("https://studev.groept.be/api/a23PT201/getItemsByCategory/"),
    CATEGORY_GET_ITEM_COUNTS("https://studev.groept.be/api/a23PT201/getCategoryItemCounts/");


    private final String url;

    Endpoints(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
