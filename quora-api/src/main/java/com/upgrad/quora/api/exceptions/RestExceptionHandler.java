package com.upgrad.quora.api.exceptions;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    /**
     * catches the AuthenticationFailedException anywhere in the application and returns the
     * message and HTTP STATUS 401
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * catches the AuthorizationFailedException anywhere in the application and returns the
     * message and HTTP STATUS 403
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    /**
     * catches the SignOutRestrictedException anywhere in the application and returns the
     * message and HTTP STATUS 401
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(SignOutRestrictedException.class)
    public ResponseEntity<ErrorResponse> signOutRestrictedException(SignOutRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    /**
     * catches the SignUpRestrictedException anywhere in the application and returns the
     * message and HTTP STATUS 409
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.CONFLICT
        );
    }

    /**
     * catches the UserNotFoundException anywhere in the application and returns the
     * message and HTTP STATUS 404
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * catches the AnswerNotFoundException anywhere in the application and returns the
     * message and HTTP STATUS 404
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(AnswerNotFoundException.class)
    public ResponseEntity<ErrorResponse> answerNotFoundException(AnswerNotFoundException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * catches the InvalidQuestionException anywhere in the application and returns the
     * message and HTTP STATUS 404
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> invalidQuestionException(InvalidQuestionException exe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    /**
     * catches the DatabaseException anywhere in the application and returns the
     * message and HTTP STATUS 500
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> databaseException(DatabaseException exe, WebRequest request) {
        return new ResponseEntity<>(
            new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    /**
     * catches the QuestionsNotFoundException anywhere in the application and returns the
     * message and HTTP STATUS 404
     * @param exe
     * @param request
     * @return
     */
    @ExceptionHandler(QuestionsNotFoundException.class)
    public ResponseEntity<ErrorResponse> questionsNotFoundException(QuestionsNotFoundException exe, WebRequest request) {
        return new ResponseEntity<>(
            new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

}
