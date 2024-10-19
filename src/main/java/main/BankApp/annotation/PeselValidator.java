package main.BankApp.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import main.BankApp.request.auth.SignupRequest;

public class PeselValidator implements ConstraintValidator<Pesel, SignupRequest> {

    private String message;
    @Override
    public void initialize(Pesel constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(SignupRequest request, ConstraintValidatorContext context) {
        String pesel = request.pesel();
        String sex = request.sex();

        if (pesel == null || !pesel.matches("\\d{11}")) {
            return false;
        }

        int[] weights = {9, 7, 3, 1, 9, 7, 3, 1, 9, 7};
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(pesel.charAt(i)) * weights[i];
        }
        int controlDigit = Character.getNumericValue(pesel.charAt(10));
        if (sum % 10 != controlDigit) {
            return false;
        }


        int genderDigit = Character.getNumericValue(pesel.charAt(9));

        if ("man".equals(sex)) {
            if (genderDigit % 2 == 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Your sex does not match your PESEL").addConstraintViolation();
                return false;
            }
        } else if ("woman".equals(sex)) {
            if (genderDigit % 2 != 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Your sex does not match your PESEL").addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}