import java.lang.reflect.Field;

/**
 * Object to store yelp review data
 */
public class Review {
	String businessId;
	String reviewIdx;
	String author;
	String authorId;
	String ratingValue;
	String datePublished;
	String content;
	String useful;
	String funny;
	String cool;
	
	public Review(String businessId, String reviewIdx, String authorId, String author, String ratingValue, String datePublished, String content, String useful, String funny, String cool){
		this.businessId = businessId;
		this.reviewIdx = reviewIdx;
		this.authorId = authorId;
		this.author = author;
		this.ratingValue = ratingValue;
		this.datePublished = datePublished;
		this.content = content;
		this.useful = useful;
		this.funny = funny;
		this.cool = cool;
	}

	public String getBusinessId() {
		return businessId;
	}
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}
	public String getReviewIdx() {
		return reviewIdx;
	}
	public void setReviewIdx(String reviewIdx) {
		this.reviewIdx = reviewIdx;
	}
	public String getRatingValue() {
		return ratingValue;
	}
	public void setRatingValue(String ratingValue) {
		this.ratingValue = ratingValue;
	}
	public String getDatePublished() {
		return datePublished;
	}
	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getUseful() {
		return useful;
	}
	public void setUseful(String useful) {
		this.useful = useful;
	}
	public String getFunny() {
		return funny;
	}
	public void setFunny(String funny) {
		this.funny = funny;
	}
	public String getCool() {
		return cool;
	}
	public void setCool(String cool) {
		this.cool = cool;
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