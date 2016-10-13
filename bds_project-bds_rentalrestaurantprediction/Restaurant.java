import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Restaurant {
	private List<Inspection> inspectionList = new ArrayList<Inspection>();
	@NonNull
	private String id;
	@NonNull
	private String name;
	@NonNull
	private String boro;
	@NonNull
	private String building;
	@NonNull
	private String street;
	@NonNull
	private String zipcode;
	@NonNull
	private String phone;
	@NonNull
	private String cuisine;
	
	public void addInspection(Inspection inspection) {
		this.inspectionList.add(inspection);
	}
}
