package com.ibm.qure.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ibm.qure.exceptions.ApplicationException;
import com.ibm.qure.model.Medicine;
import com.ibm.qure.model.ResponseMessage;
import com.ibm.qure.service.MedicineService;
import com.ibm.qure.exceptions.QureApplicationException;

@RestController
@RequestMapping("/medicines")
public class MedicineController {

	private static Logger log = LoggerFactory.getLogger(MedicineController.class);
	@Autowired
	MedicineService medicineService;

	// List All Medicines GET /medicines
	@GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin("*")
	public List<Medicine> getAllMedicines() throws QureApplicationException {

		return medicineService.getAll();
	}

	// List Medicine for given Id GET /medicines/{id}
	@GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin("*")
	public Medicine getMedicine(@PathVariable String id) throws QureApplicationException{
		return medicineService.get(id);
	}

	// Create Medicine POST /medicines
	@PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	@CrossOrigin("*")
	public ResponseEntity<ResponseMessage> createMedicine(@RequestBody @Valid Medicine medicine)
			throws URISyntaxException, ApplicationException, QureApplicationException {

		ResponseMessage resMsg;

		boolean x = medicineService.create(medicine);

		if (x) {
			resMsg = new ResponseMessage("Success", new String[] { "Medicine created successfully" });
			log.debug("Medicine created successfully");
		} else {
			resMsg = new ResponseMessage("Failure", new String[] { "Medicine failed to create" });
			log.debug("Medicine failed to create");
		}
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(medicine.getMedicineId()).toUri();

		return ResponseEntity.created(location).body(resMsg);

	}

	// Update Medicine PUT /medicines/{id}
	@PutMapping(value = "/{id}")
	@CrossOrigin("*")
	public ResponseEntity<ResponseMessage> updateMedicine(@PathVariable String id,
			@RequestBody Medicine updatedMedicine)
			throws URISyntaxException, ApplicationException, QureApplicationException {
		ResponseMessage resMsg;

		updatedMedicine.setMedicineId(id);
		boolean x = medicineService.update(updatedMedicine);
		if (x) {
			resMsg = new ResponseMessage("Success", new String[] { "Medicine updated successfully" });
			log.debug("Medicine updated successfully");
		} else {
			resMsg = new ResponseMessage("Failure", new String[] { "Medicine failed to update" });
			log.debug("Medicine failed to update");
		}
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(updatedMedicine.getMedicineId()).toUri();

		return ResponseEntity.created(location).body(resMsg);
	}

	// Delete Medicine DELETE /medicines/{id}
	@DeleteMapping("/{id}")
	@CrossOrigin("*")
	public ResponseEntity<ResponseMessage> deleteMedicine(@PathVariable String id)
			throws URISyntaxException, ApplicationException, QureApplicationException {
		boolean x = medicineService.delete(id);
		ResponseMessage resMsg;
		if (x) {
			resMsg = new ResponseMessage("Success", new String[] { "Medicine deleted successfully" });
			log.debug("Medicine deleted successfully");
		} else {
			resMsg = new ResponseMessage("Failure", new String[] { "Failed to delete medicine" });
			log.debug("Failed to delete medicine");
		}
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();

		return ResponseEntity.created(location).body(resMsg);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseMessage> handleValidationExcpetion(MethodArgumentNotValidException e) {

		List<ObjectError> errors = e.getBindingResult().getAllErrors();
		int size = errors.size();
		String[] errorMsgs = new String[size];

		for (int i = 0; i < size; i++) {
			errorMsgs[i] = errors.get(i).getDefaultMessage();
		}

		ResponseMessage resMsg = new ResponseMessage("Failure", errorMsgs);
		return ResponseEntity.badRequest().body(resMsg);
	}

	@ExceptionHandler(QureApplicationException.class)
	public ResponseEntity<ResponseMessage> handleQureApplicationExcpetion(Exception e) {
		log.error("Error Occured:{}", e.getMessage(), e);
		ResponseMessage resMsg = new ResponseMessage("Failure", new String[] { e.getMessage() },
				ExceptionUtils.getStackTrace(e));
		return ResponseEntity.badRequest().body(resMsg);
	}

}
