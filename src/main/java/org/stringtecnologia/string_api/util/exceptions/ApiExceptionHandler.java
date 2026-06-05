package org.stringtecnologia.string_api.util.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Exceções de domínio da aplicação
     */
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(
            DomainException ex,
            HttpServletRequest request
    ) {

        ProblemDetail problem = ProblemDetail.forStatus(ex.getStatus());

        problem.setTitle("Erro de negócio");
        problem.setDetail(ex.getMessage());

        problem.setType(URI.create("https://api.sicad/errors/" + ex.getErrorCode()));
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    /**
     * Acesso negado (Spring Security)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problem.setTitle("Acesso negado");
        problem.setDetail("Você não possui permissão para acessar este recurso.");

        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", "ACESSO_NEGADO");
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    /**
     * Violação de integridade do banco
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("Violação de integridade");
        problem.setDetail("Operação viola restrição de dados.");

        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", "DATA_INTEGRITY");
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("Erro de regra de negócio");
        problem.setDetail(ex.getMessage());
        problem.setInstance(URI.create(request.getRequestURI()));

        problem.setProperty("errorCode", ex.getErrorCode());
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Erro inesperado");
        problem.setDetail("Ocorreu um erro interno. Entre em contato com o suporte.");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}