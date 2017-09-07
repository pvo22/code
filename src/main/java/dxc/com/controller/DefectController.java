package dxc.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import dxc.com.group.model.Defect;
import dxc.com.group.service.DefectDateService;
import dxc.com.group.service.DefectService;

@RestController
public class DefectController {
	
	@Autowired
	private DefectService defectServices;
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/defects/{name}", method = RequestMethod.GET)
	public Defect getDefects(@PathVariable String name) throws Exception {
		return defectServices.getDefectByNameProject(name);
	}
	
	@Autowired
	private DefectDateService defectdateServices;
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/defectdates/{name}", method = RequestMethod.GET)
	public String getDefectDates(@PathVariable String name) throws Exception {
		return defectdateServices.getDefectDateByNameProject(name);
	}
}