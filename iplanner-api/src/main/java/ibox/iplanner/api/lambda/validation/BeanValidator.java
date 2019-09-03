package ibox.iplanner.api.lambda.validation;

import ibox.iplanner.api.lambda.exception.InvalidInputException;
import ibox.iplanner.api.model.ApiError;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

import static ibox.iplanner.api.util.ApiErrorConstants.ERROR_BAD_REQUEST;
import static ibox.iplanner.api.util.ApiErrorConstants.SC_BAD_REQUEST;

public class BeanValidator {

    public BeanValidator() {

    }

    public <T>void validate(T bean) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(bean);

        if (!violations.isEmpty()) {
            ApiError error = ApiError.builder()
                    .error(ERROR_BAD_REQUEST)
                    .message("%s is missing required fields: " + bean.getClass().getSimpleName())
                    .status(SC_BAD_REQUEST)
                    .errorDetails(violations.stream().map(e-> e.getMessage()).collect(Collectors.toList()))
                    .build();
            throw new InvalidInputException(String.format("Input %s invalid", bean.getClass().getSimpleName()), error);
        }

    }
}
