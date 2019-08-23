package ibox.iplanner.api.exception;

import ibox.iplanner.api.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // @Validate For Validating Path Variables and Request Parameters
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object>  handleConstraintViolationException(ConstraintViolationException ex) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiError error = new ApiError();
        error.setStatus(status.value()+ "");
        error.setError(status.getReasonPhrase());
        error.setTimestamp(Instant.now());
        error.setMessage(ex.getMessage());

        List<String> details = error.getErrorDetails();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        violations.stream().forEach(e-> {
            details.add(e.getMessage());
        });
        headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        return new ResponseEntity<>(error, headers, status);
    }

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                 HttpHeaders headers,
                                 HttpStatus status, WebRequest request) {

        ApiError error = new ApiError();
        error.setStatus(status.value()+ "");
        error.setError(status.getReasonPhrase());
        error.setTimestamp(Instant.now());
        error.setMessage(ex.getMessage());

        List<String> details = error.getErrorDetails();

        //Get all errors
        details.addAll(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList()));

        return new ResponseEntity<>(error, headers, status);

    }

}
