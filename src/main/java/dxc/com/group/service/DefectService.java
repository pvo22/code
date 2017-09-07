package dxc.com.group.service;

import dxc.com.group.model.Defect;

public interface DefectService {
	public Defect getDefectByNameProject(String nameProject) throws Exception;
}
