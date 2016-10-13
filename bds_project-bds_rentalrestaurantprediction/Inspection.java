import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Inspection {
	private String inspectionDate;
	private String action;
	private String violationCode;
	private String violationDescription;
	private String criticalFlag;
	private String score;
	private String grade;
	private String gradeDate;
	private String recordDate;
	private String inspectionType;

}
