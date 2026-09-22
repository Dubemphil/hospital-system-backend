package com.dentalmanagement.advice;

import java.util.NoSuchElementException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.dentalmanagement.dto.CommanApiResponse;
import com.dentalmanagement.exception.AppointmentNotFoundException;
import com.dentalmanagement.exception.UserNotFoundException;

@ControllerAdvice
public class MyControllerAdvice extends ResponseEntityExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(MyControllerAdvice.class);
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
		return new ResponseEntity<String>("User Not Found, please send proper data", HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AppointmentNotFoundException.class)
	public ResponseEntity<String> handleAppointmentNotFoundException(AppointmentNotFoundException appointmentNotFoundException) {
		return new ResponseEntity<String>("Appointment Not Found, please send proper data", HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException noSuchElementException) {
		return new ResponseEntity<String>("No Value is present in DB, Please change your request", HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommanApiResponse> handleGlobalException(Exception ex) {
		String incidentId = UUID.randomUUID().toString();
		LOG.error("Unhandled exception [Incident: {}] - {}", incidentId, ex.getMessage());
		CommanApiResponse response = new CommanApiResponse();
		response.setResponseCode(com.dentalmanagement.utility.Constants.ResponseCode.FAILED.value());
		response.setResponseMessage("An error occurred while processing your request. Incident ID: " + incidentId);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<Object>("Please change your Http Method type", HttpStatus.NOT_FOUND);
	}

}
