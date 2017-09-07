package dxc.com.group.model;

public class DefectDate {
	
	private String nameProject;
	private String date;
	private int totalClosed;
	private int totalOpened;
	private int totalResolved;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNameProject() {
		return nameProject;
	}

	public void setNameProject(String nameProject) {
		this.nameProject = nameProject;
	}

	public int getTotalClosed() {
		return totalClosed;
	}

	public void setTotalClosed(int totalClosed) {
		this.totalClosed = totalClosed;
	}

	public int getTotalOpened() {
		return totalOpened;
	}

	public void setTotalOpened(int totalOpened) {
		this.totalOpened = totalOpened;
	}

	public int getTotalResolved() {
		return totalResolved;
	}

	public void setTotalResolved(int totalResolved) {
		this.totalResolved = totalResolved;
	}

}
