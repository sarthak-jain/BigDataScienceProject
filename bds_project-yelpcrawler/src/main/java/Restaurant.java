
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

/**
 * Object to store yelp restaurant data
 */
public class Restaurant {

	String businessId;
	String name;
	String address;
	String phone;
	String price;
	String reviewCount;
	String rating;
	String category;
	String openHour;
	String yelpURL;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(String reviewCount) {
		this.reviewCount = reviewCount;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getOpenHour() {
		return openHour;
	}
	public void setOpenHour(String openHour) {
		this.openHour = openHour;
	}
	public String getYelpURL() {
		return yelpURL;
	}
	public void setYelpURL(String yelpURL) {
		this.yelpURL = yelpURL;
	}

	public String[] getValueList() {
		//determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();
		String[] result = new String[fields.length];

		//print field names paired with their values
		for (int i=0; i<fields.length; i++) {
			try {
				result[i] = fields[i].get(this).toString();
			} catch ( IllegalAccessException ex ) {
				System.out.println(ex);
			}
		}
		return result;
	}
}
