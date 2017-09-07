package dxc.com.group.model;

public class Defect {

	private String nameProject;
	private int totalClosed;
	private int totalOpened;
	private int totalResolved;
	private int totalCritical;
	private int totalMajor;
	private int totalMinor;

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

	public int getTotalCritical() {
		return totalCritical;
	}

	public void setTotalCritical(int totalCritical) {
		this.totalCritical = totalCritical;
	}

	public int getTotalMajor() {
		return totalMajor;
	}

	public void setTotalMajor(int totalMajor) {
		this.totalMajor = totalMajor;
	}

	public int getTotalMinor() {
		return totalMinor;
	}

	public void setTotalMinor(int totalMinor) {
		this.totalMinor = totalMinor;
	}

}
