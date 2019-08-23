package ibox.iplanner.api.exception;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

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

    // Error Handling for @Valid
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

    // AWS DynamoDB Error Handling
    @ExceptionHandler(AmazonDynamoDBException.class)
    protected ResponseEntity<Object> handleAmazonDynamoDBException(AmazonDynamoDBException ex) {

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiError error = new ApiError();
        error.setStatus(status.value()+ "");
        error.setError(status.getReasonPhrase());
        error.setTimestamp(Instant.now());
        error.setMessage(ex.getMessage());

        List<String> details = error.getErrorDetails();
        details.add(String.format("AWS Returned HTTP status code: %s", ex.getStatusCode()));
        details.add(String.format("AWS Returned error code: %s", ex.getErrorCode()));
        details.add(String.format("Detailed error message from the service: %s", ex.getErrorMessage()));
        details.add(String.format("AWS service name: %s", ex.getServiceName()));
        details.add(String.format("AWS request ID for the failed request: %s", ex.getRequestId()));

        headers.add("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        return new ResponseEntity<>(error, headers, status);
    }
}
