package be.kuleuven.gt.stockcheck.data.model;

public class categoryModel {
     String categoryName;
     int image;
    int itemCount;

    public categoryModel(String categoryName, int image, int itemCount) {
        this.categoryName = categoryName;
        this.image = image;
        this.itemCount = itemCount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getImage() {
        return image;
    }

    public int getItemCount() {
        return itemCount;
    }
}
