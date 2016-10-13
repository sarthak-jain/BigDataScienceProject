import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RestaurantFirstLastInspection {
	private String restaurantId;
	private String firstInspectionDate;
	private String lastInspectionDate;
	
	@Override
	public String toString() {
		return restaurantId + ',' + firstInspectionDate + ',' + lastInspectionDate;
	}
}
